package com.shambonik.meat.controllers;

import com.shambonik.meat.models.Role;
import com.shambonik.meat.services.CartService;
import com.shambonik.meat.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final ProductService productService;
    private final CartService cartService;

    @GetMapping
    public String getPage(@CookieValue(value = "meatCart", required = false) String meatCart, Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("authorized", auth.getPrincipal()!="anonymousUser");
        model.addAttribute("admin", auth.getAuthorities().contains(Role.ADMIN));
        model.addAttribute("list", productService.getProducts());
        model.addAttribute("cartSize", cartService.getCartSize(meatCart));
        return "index";
    }

    @PostMapping("/add_to_cart/{id}")
    public String addToCart(@CookieValue(value = "meatCart", required = false) String meatCart, @PathVariable("id") long id, HttpServletResponse response){
        return cartService.addToCart(meatCart, id, response);
    }
}
