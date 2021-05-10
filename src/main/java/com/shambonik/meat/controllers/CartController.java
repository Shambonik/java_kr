package com.shambonik.meat.controllers;

import com.shambonik.meat.Pair;
import com.shambonik.meat.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public String getCartPage(@CookieValue(value = "meatCart", required = false) String meatCart, Model model){
        return cartService.getCart(meatCart, model);
    }

    @GetMapping("/edit_count/{id}")
    public String getCartPage(@PathVariable("id") long id, @RequestParam(value = "count") int count,
                              @CookieValue(value = "meatCart") String meatCart,  HttpServletResponse response){
        return cartService.editProductCount(id, count, meatCart, response);
    }

}
