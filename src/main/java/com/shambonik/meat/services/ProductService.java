package com.shambonik.meat.services;

import com.shambonik.meat.dto.CategoryFilters;
import com.shambonik.meat.models.Product;
import com.shambonik.meat.models.ProductCount;
import com.shambonik.meat.models.Product_Category;
import com.shambonik.meat.repositories.ProductRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepo productRepo;
    @Value("${upload.path}")
    private String uploadPath;

    public List<Product> getActiveProducts(){
        return productRepo.findByActive(true);
    }

    public List<Product> getProducts(){
        return productRepo.findAll();
    }

    public CategoryFilters getCategoryFilters(){
        CategoryFilters filters = new CategoryFilters();
        for(Product_Category i : Product_Category.getCategories()){
            filters.getList().add(new CategoryFilters.Pair(i.name()));
        }
        return filters;
    }

    public String filter(CategoryFilters filters, RedirectAttributes redirectAttributes){
        CategoryFilters categoryFilters = getCategoryFilters();
        List<Product> productList = new ArrayList<>();
        int falseCount = 0;
        for(int i = 0; i < categoryFilters.getList().size(); i++) {
            if(filters.getList().get(i).isChecked()) {
                categoryFilters.getList().get(i).setChecked(true);
                productList.addAll(productRepo.findByCategoryAndActive(
                        Product_Category.valueOf(categoryFilters.getList().get(i).getName()), true));
            }
            else falseCount++;
        }
        if(falseCount<categoryFilters.getList().size())
            redirectAttributes.addFlashAttribute("list", productList);
        redirectAttributes.addFlashAttribute("categories", categoryFilters);
        return "redirect:/";
    }

    public String addProduct(Product product, RedirectAttributes redirectAttributes, MultipartFile file){
        boolean flagOfErrors = false;
        if(product.getName().equals("")){
            flagOfErrors = true;
            redirectAttributes.addFlashAttribute("nameErr", "?????? ???? ?????????? ???????? ????????????");
        }
        if(product.getDescription().equals("")){
            flagOfErrors = true;
            redirectAttributes.addFlashAttribute("descriptionErr", "???????????????? ???? ?????????? ???????? ????????????");
        }
        if(product.getCategory().isEmpty()){
            flagOfErrors = true;
            redirectAttributes.addFlashAttribute("categoryErr", "???????????????? ?????????????????? ????????????");
        }
        if(!file.getOriginalFilename().equals("")) {
            String uuid = UUID.randomUUID().toString();
            String nameOfFile = uuid + file.getOriginalFilename();
            String filePath = uploadPath + "/" + nameOfFile;

            product.setImageName(nameOfFile);
            try {
                file.transferTo(new File(filePath));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else {
            flagOfErrors = true;
            redirectAttributes.addFlashAttribute("fileErr", "???????? ?????????? ???? ?????????? ???????? ????????????");
        }

        if (!flagOfErrors) {
            productRepo.save(product);
            return "redirect:/admin/products";
        }
        return "redirect:/admin/products/add_product";
    }

    public String editProduct(Product product, RedirectAttributes redirectAttributes, long id){
        boolean flagOfErrors = false;
        Product productOriginal = productRepo.findProductById(id);
        if(product.getName().equals("")){
            flagOfErrors = true;
            redirectAttributes.addFlashAttribute("nameErr", "?????? ???? ?????????? ???????? ????????????");
        }
        if(product.getDescription().equals("")){
            flagOfErrors = true;
            redirectAttributes.addFlashAttribute("descriptionErr", "???????????????? ???? ?????????? ???????? ????????????");
        }
        if(product.getCategory().isEmpty()){
            flagOfErrors = true;
            redirectAttributes.addFlashAttribute("categoryErr", "???????????????? ?????????????????? ????????????");
        }
        if (!flagOfErrors) {
            productOriginal.setAll(product.getName(), product.getDescription(),
                    product.getPrice(), product.isActive(),
                    product.getCategory(), product.getCount());
            productRepo.save(productOriginal);
            return "redirect:/admin/products";
        }
        return "redirect:/admin/products/edit_product/"+product.getId();
    }

    private void deleteImage(Product product){
        File file = new File(uploadPath + "/" + product.getImageName());
        file.delete();
    }

    public void changeProductActive(Long id){
        Product product = productRepo.findProductById(id);
        product.setActive(!product.isActive());
        productRepo.save(product);
    }

    public String getEditPage(Model model, long id) {
        model.addAttribute("product", productRepo.findProductById(id));
        model.addAttribute("categories", Product_Category.getCategories());
        return "admin/products/edit_product";
    }

    public String getEditImagePage(Model model, long id){
        model.addAttribute("product", productRepo.findProductById(id));
        return "admin/products/edit_product_image";
    }

    public String editImage(RedirectAttributes redirectAttributes, long id, MultipartFile file){
        Product product = productRepo.findProductById(id);
        if(!file.getOriginalFilename().equals("")) {
            deleteImage(product);
            String uuid = UUID.randomUUID().toString();
            String nameOfFile = uuid + file.getOriginalFilename();
            String filePath = uploadPath + "/" + nameOfFile;

            product.setImageName(nameOfFile);
            try {
                file.transferTo(new File(filePath));
            } catch (IOException e) {
                e.printStackTrace();
            }

            productRepo.save(product);
            return "redirect:/admin/products";
        }
        redirectAttributes.addFlashAttribute("fileErr", "???????? ?????????? ???? ?????????? ???????? ????????????");
        return "redirect:/admin/products/product/edit_product/" + id + "/image";
    }

    public void reduceCount(ProductCount productCount){
        productCount.getProduct().setCount(productCount.getProduct().getCount()-productCount.getCount());
        productRepo.save(productCount.getProduct());
    }

    public Product getProductById(long id){
        return productRepo.findProductById(id);
    }
}
