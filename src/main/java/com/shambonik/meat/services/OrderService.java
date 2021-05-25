package com.shambonik.meat.services;

import com.shambonik.meat.models.Order;
import com.shambonik.meat.models.Order_Status;
import com.shambonik.meat.models.ProductCount;
import com.shambonik.meat.models.User;
import com.shambonik.meat.repositories.OrderRepo;
import com.shambonik.meat.repositories.ProductCountRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.function.ToLongFunction;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final ProductService productService;
    private final UserService userService;
    private final CartService cartService;
    private final OrderRepo orderRepo;
    private final ProductCountRepo productCountRepo;
    @Value("${cookie.orders.name}")
    private String cookieName;

    public Object[] getOrders() {
        return orderRepo.findAll().stream().sorted(Comparator.comparingLong(Order::getId).reversed()).toArray();
    }

    public void clearOrderCookie(HttpServletResponse response){
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setPath("/");
        cookie.setMaxAge(86400);
        response.addCookie(cookie);
    }

    private String[] getListOfIDs(String orderCookie){
        return orderCookie.split("\\.");
    }

    public String cancelOrder(long id){
        Order order = orderRepo.findOrderById(id);
        if(!order.getStatus().contains("COMPLETED") && !order.getStatus().contains("CANCELED")){
            order.setStatus(Collections.singleton(Order_Status.CANCELED));
            orderRepo.save(order);
        }
        return "redirect:/order/list";
    }

    public void synchronizeOrders(User user, String orderCookie, HttpServletResponse response){
        if(user != null){
            if(orderCookie != null){
                String[] orderIDs = getListOfIDs(orderCookie);
                for(String idStr : orderIDs){
                    if(!idStr.equals("")) {
                        Order order = orderRepo.findOrderById(Long.parseLong(idStr));
                        order.setUser(user);
                        orderRepo.save(order);
                    }
                }
            }
            clearOrderCookie(response);
        }
    }

    public String getChangeStatusPage(long id, Model model) {
        model.addAttribute("order", orderRepo.findOrderById(id));
        model.addAttribute("statuses", Order_Status.getStatuses());
        return "/admin/orders/change_status";
    }

    public String getOrderListPage(User user, String orderCookie, HttpServletResponse response, Model model){
        synchronizeOrders(user, orderCookie, response);
        Set<Order> orders = new HashSet<>();
        if(user != null){
            orders = new HashSet<>(userService.getUserById(user.getId()).getOrders());
        }
        else if(orderCookie != null){
            String[] orderIDstr = getListOfIDs(orderCookie);
            for(String orderStr : orderIDstr){
                if(!orderStr.equals(""))
                    orders.add(orderRepo.findOrderById(Long.parseLong(orderStr)));
            }
        }
        model.addAttribute("list", orders.stream().sorted(Comparator.comparingLong(Order::getId).reversed()).toArray());
        return"order_list";
    }

    public String changeStatus(long id, Order order){
        Order original_order = orderRepo.findOrderById(id);
        original_order.setStatus(order.getStatus());
        orderRepo.save(original_order);
        return "redirect:/admin/orders";
    }

    public String saveOrder(User user, Order order, String meatCart, String orderCookie,
                            Map<String, Object> model, HttpServletResponse response){
        if((order.getName()==null || order.getAddress()==null || order.getEmail() == null || order.getPhone() == null) ||
                (order.getName().equals("") || order.getAddress().equals("") || order.getEmail().equals("")
                        || order.getPhone().equals(""))){
            model.put("message", "Не все поля заполены!");
            return "order";
        }
        CartService.Pair pair = cartService.getProducts(meatCart);
        List<ProductCount> products = pair.getProductCounts();
        if(products != null) {
            for (ProductCount product : products) {
                if(product.getCount() > product.getProduct().getCount()){
                    model.put("message", "На складе нет достаточного количества продукта \""+
                            product.getProduct().getName() + "\"");
                    return "order";
                }
            }
            order.setUser(user);
            order.setTotalPrice(pair.getTotalPrice());
            order.setStatus(Collections.singleton(Order_Status.PROCESSING));
            orderRepo.save(order);
            for (ProductCount product : products) {
                productService.reduceCount(product);
                product.setOrder(order);
                productCountRepo.save(product);
            }
            cartService.clearCart(response);
            Cookie cookie = new Cookie(cookieName,
                    (orderCookie != null ? orderCookie : "") + order.getId() + ".");
            cookie.setPath("/");
            cookie.setMaxAge(1000000);
            response.addCookie(cookie);
            return "redirect:/";
        }
        model.put("message", "Empty cart!");
        return "order";
    }
}
