package com.shambonik.meat.controllers;

import com.shambonik.meat.models.Order;
import com.shambonik.meat.models.Order_Status;
import com.shambonik.meat.models.Product;
import com.shambonik.meat.models.Product_Category;
import com.shambonik.meat.services.OrderService;
import com.shambonik.meat.services.ProductService;
import com.shambonik.meat.services.UserService;
import lombok.RequiredArgsConstructor;
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
    public String getUsersPage(Model model){
        model.addAttribute("list", userService.getUsers());
        return "admin/users/users";
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

    @PostMapping("/products/delete_product/{id}")
    public String deleteProduct(@PathVariable("id") long id){
        productService.deleteProduct(id);
        return "redirect:/admin";
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
