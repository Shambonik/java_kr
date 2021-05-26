package com.shambonik.meat.controllers;

import com.shambonik.meat.models.Order;
import com.shambonik.meat.models.User;
import com.shambonik.meat.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public String getOrderPage(@AuthenticationPrincipal User user, Model model){
        Order order = new Order();
        if(user!=null){
            order.setData(user.getName(), user.getAddress(), user.getEmail(), user.getPhone());
        }
        model.addAttribute("order", order);
        return "order";
    }

    @PostMapping
    public String saveOrder(@AuthenticationPrincipal User user, Order order,
                            @CookieValue(value = "meatCart") String meatCart,
                            @CookieValue(value = "orders", required = false) String orderCookie,
                            Map<String, Object> model, HttpServletResponse response){
        return orderService.saveOrder(user, order, meatCart, orderCookie, model, response);
    }

    @PostMapping("/list/cancel/{id}")
    public String cancelOrder(@PathVariable("id") long id){
        return orderService.cancelOrder(id);
    }


    @GetMapping("/synchronize_orders")
    public String synchronizeOrders(@AuthenticationPrincipal User user,
                                    @CookieValue(value = "orders", required = false) String orderCookie,
                                    HttpServletResponse response){
        orderService.synchronizeOrders(user, orderCookie, response);
        return "redirect:/";
    }

    @GetMapping("/list")
    public String getOrderListPage(@AuthenticationPrincipal User user,
                                   @CookieValue(value = "orders", required = false) String orderCookie,
                                   HttpServletResponse response, Model model){
        return orderService.getOrderListPage(user, orderCookie, response, model);
    }


}
