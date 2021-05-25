package com.shambonik.meat.controllers;

import com.shambonik.meat.dto.ChangeRole;
import com.shambonik.meat.models.*;
import com.shambonik.meat.services.OrderService;
import com.shambonik.meat.services.ProductService;
import com.shambonik.meat.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final ProductService productService;
    private final UserService userService;
    private final OrderService orderService;

    @GetMapping("/users")
    public String getUsersPage(@AuthenticationPrincipal User user, Model model){
        model.addAttribute("list", userService.getUsers());
        model.addAttribute("roles", Role.values());
        model.addAttribute("changeRole", new ChangeRole());
        model.addAttribute("currentID", user.getId());
        return "admin/users/users";
    }

    @PostMapping("/users/change_role/{id}")
    public String changeUserRole(@AuthenticationPrincipal User user, @PathVariable("id") long id, ChangeRole changeRole){
        return userService.changeUserRole(user, id, changeRole);
    }

    @GetMapping("/orders")
    public String getOrdersPage(Model model){
        model.addAttribute("list", orderService.getOrders());
        return "admin/orders/orders";
    }

    @PostMapping("/orders/changeStatus/{id}")
    public String changeStatus(@PathVariable("id") long id, Order order){
        return orderService.changeStatus(id, order);
    }


    @GetMapping("/orders/changeStatus/{id}")
    public String getChangeStatusPage(@PathVariable("id") long id, Model model){
        return orderService.getChangeStatusPage(id, model);
    }


    @GetMapping("/products")
    public String getProductsPage(Model model){
        model.addAttribute("list", productService.getProducts());
        return "admin/products/products";
    }

    @GetMapping("/products/add_product")
    public String getAddPage(Model model){
        model.addAttribute("product", new Product());
        model.addAttribute("categories", Product_Category.getCategories());
        return "admin/products/add_product";
    }


    @GetMapping("/products/edit_product/{id}")
    public String getEditPage(Model model, @PathVariable("id") long id){
        return productService.getEditPage(model, id);
    }

    @PostMapping("/products/add_product")
    public String addProduct(Product product, RedirectAttributes redirectAttributes, @RequestParam(name = "image") MultipartFile file){
        return productService.addProduct(product, redirectAttributes, file);
    }

    @PostMapping("/products/edit_product/{id}")
    public String addProduct(Product product, RedirectAttributes redirectAttributes, @PathVariable("id") long id){
        return productService.editProduct(product, redirectAttributes, id);
    }

    @PostMapping("/products/change_product_active/{id}")
    public String changeProductActive(@PathVariable("id") long id){
        productService.changeProductActive(id);
        return "redirect:/admin/products";
    }

    @GetMapping("/products/edit_product/{id}/image")
    public String getEditImagePage(Model model, @PathVariable("id") long id){
        return productService.getEditImagePage(model, id);
    }

    @PostMapping("/products/edit_product/{id}/image")
    public String editImage(RedirectAttributes redirectAttributes, @PathVariable("id") long id, @RequestParam(name = "image") MultipartFile file){
        return productService.editImage(redirectAttributes, id, file);
    }
}
