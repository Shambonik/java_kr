package com.shambonik.meat.services;

import com.shambonik.meat.models.Order;
import com.shambonik.meat.models.ProductCount;
import com.shambonik.meat.repositories.OrderRepo;
import com.shambonik.meat.repositories.ProductCountRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final ProductService productService;
    private final CartService cartService;
    private final OrderRepo orderRepo;
    private final ProductCountRepo productCountRepo;

    public List<Order> getOrders(){
        return orderRepo.findAll();
    }

    public String saveOrder(Order order, String meatCart, Map<String, Object> model, HttpServletResponse response){
        if((order.getName()==null || order.getAddress()==null || order.getEmail() == null || order.getPhone() == null) ||
                (order.getName().equals("") || order.getAddress().equals("") || order.getEmail().equals("")
                        || order.getPhone().equals(""))){
            model.put("message", "Не все поля заполены!");
            return "order";
        }
        List<ProductCount> products = cartService.getProducts(meatCart);
        if(products != null) {
            for (ProductCount product : products) {
                if(product.getCount() > product.getProduct().getCount()){
                    model.put("message", "На складе нет достаточного количества продукта \""+
                            product.getProduct().getName() + "\"");
                    return "order";
                }
            }
            orderRepo.save(order);
            for (ProductCount product : products) {
                productService.reduceCount(product);
                product.setOrder(order);
                productCountRepo.save(product);
            }
            cartService.clearCart(response);
            return "redirect:/";
        }
        model.put("message", "Empty cart!");
        return "order";
    }
}
