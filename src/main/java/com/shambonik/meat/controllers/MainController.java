package com.shambonik.meat.controllers;

import com.shambonik.meat.dto.CategoryFilters;
import com.shambonik.meat.models.Role;
import com.shambonik.meat.services.CartService;
import com.shambonik.meat.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final ProductService productService;
    private final CartService cartService;

    @GetMapping
    public String getPage(@CookieValue(value = "meatCart", required = false) String meatCart,
                          HttpServletRequest request,
                          HttpServletResponse response, Model model){
        Map<String, ?> map = RequestContextUtils.getInputFlashMap(request);
        if(map != null) {
            for (Map.Entry<String, ?> entry : map.entrySet()) {
                model.addAttribute(entry.getKey(), entry.getValue());
            }
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("authorized", auth.getPrincipal()!="anonymousUser");
        model.addAttribute("admin", auth.getAuthorities().contains(Role.ADMIN));
        model.addAttribute("cartSize", cartService.getCartSize(meatCart, response));
        if(!model.containsAttribute("list"))
            model.addAttribute("list", productService.getActiveProducts());
        if(!model.containsAttribute("categories"))
            model.addAttribute("categories", productService.getCategoryFilters());
        return "index";
    }

    @PostMapping("/add_to_cart/{id}")
    public String addToCart(@CookieValue(value = "meatCart", required = false) String meatCart, @PathVariable("id") long id, HttpServletResponse response){
        return cartService.addToCart(meatCart, id, response);
    }

    @PostMapping("/filter")
    public String filterProducts(CategoryFilters categories, RedirectAttributes redirectAttributes){
        return productService.filter(categories, redirectAttributes);
    }
}
