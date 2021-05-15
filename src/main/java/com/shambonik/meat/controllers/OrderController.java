package com.shambonik.meat.controllers;

import com.shambonik.meat.models.Order;
import com.shambonik.meat.models.User;
import com.shambonik.meat.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

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
    public String saveOrder(Order order, @CookieValue(value = "meatCart") String meatCart, Map<String, Object> model, HttpServletResponse response){
        return orderService.saveOrder(order, meatCart, model, response);
    }
}
