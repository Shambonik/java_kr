package com.shambonik.meat.services;

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
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepo productRepo;
    @Value("${upload.path}")
    private String uploadPath;

    public List<Product> getProducts(){
        return productRepo.findAll();
    }

    public String addProduct(Product product, RedirectAttributes redirectAttributes, MultipartFile file){
        boolean flagOfErrors = false;
        if(product.getName().equals("")){
            flagOfErrors = true;
            redirectAttributes.addFlashAttribute("nameErr", "Имя не может быть пустым");
        }
        if(product.getDescription().equals("")){
            flagOfErrors = true;
            redirectAttributes.addFlashAttribute("descriptionErr", "Описание не может быть пустым");
        }
        if(product.getCategory().isEmpty()){
            flagOfErrors = true;
            redirectAttributes.addFlashAttribute("categoryErr", "Выберите категорию товара");
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
            redirectAttributes.addFlashAttribute("fileErr", "Поле фалйа не может быть пустым");
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
            redirectAttributes.addFlashAttribute("nameErr", "Имя не может быть пустым");
        }
        if(product.getDescription().equals("")){
            flagOfErrors = true;
            redirectAttributes.addFlashAttribute("descriptionErr", "Описание не может быть пустым");
        }
        if(product.getCategory().isEmpty()){
            flagOfErrors = true;
            redirectAttributes.addFlashAttribute("categoryErr", "Выберите категорию товара");
        }
        if (!flagOfErrors) {
            productOriginal.setAll(product.getName(), product.getDescription(), product.getCategory(), product.getCount());
            productRepo.save(productOriginal);
            return "redirect:/admin/products";
        }
        return "redirect:/admin/products/edit_product/"+product.getId();
    }

    private void deleteImage(Product product){
        File file = new File(uploadPath + "/" + product.getImageName());
        file.delete();
    }

    public void deleteProduct(Long id){
        Product product = productRepo.findProductById(id);
        deleteImage(product);
        productRepo.delete(product);
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
        redirectAttributes.addFlashAttribute("fileErr", "Поле фалйа не может быть пустым");
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
