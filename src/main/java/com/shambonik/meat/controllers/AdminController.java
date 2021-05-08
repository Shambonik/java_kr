package com.shambonik.meat.controllers;

import com.shambonik.meat.models.Product;
import com.shambonik.meat.models.Product_Category;
import com.shambonik.meat.repositories.ProductRepo;
import com.shambonik.meat.services.ProductService;
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

    @GetMapping
    public String getMainPage(Model model){
        model.addAttribute("list", productService.getProducts());
        return "admin/main";
    }

    @GetMapping("/add_product")
    public String getAddPage(Model model){
        model.addAttribute("product", new Product());
        model.addAttribute("categories", Product_Category.getCategories());
        return "admin/add_product";
    }


    @GetMapping("/edit_product/{id}")
    public String getEditPage(Model model, @PathVariable("id") long id){
        return productService.getEditPage(model, id);
    }

    @PostMapping("/add_product")
    public String addProduct(Product product, RedirectAttributes redirectAttributes, @RequestParam(name = "image") MultipartFile file){
//        productService.addProduct(product, file);
        return productService.addProduct(product, redirectAttributes, file);
    }

    @PostMapping("/edit_product/{id}")
    public String addProduct(Product product, RedirectAttributes redirectAttributes, @PathVariable("id") long id){
        return productService.editProduct(product, redirectAttributes, id);
    }

    @PostMapping("/product/{id}")
    public String deleteProduct(@PathVariable("id") long id){
        productService.deleteProduct(id);
        return "redirect:/admin";
    }

    @GetMapping("/product/edit_product/{id}/image")
    public String getEditImagePage(Model model, @PathVariable("id") long id){
        return productService.getEditImagePage(model, id);
    }

    @PostMapping("/product/edit_product/{id}/image")
    public String editImage(RedirectAttributes redirectAttributes, @PathVariable("id") long id, @RequestParam(name = "image") MultipartFile file){
        return productService.editImage(redirectAttributes, id, file);
    }
}
