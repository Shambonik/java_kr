package com.shambonik.meat.services;

import com.shambonik.meat.models.Product;
import com.shambonik.meat.repositories.ProductRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
    String uploadPath;

    public List<Product> getProducts(){
        return productRepo.findAll();
    }

    public String addProduct(Product product, RedirectAttributes redirectAttributes, MultipartFile file){
        boolean flagOfErrors = false;
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

        } else {
            flagOfErrors = true;
            redirectAttributes.addFlashAttribute("fileErr", "Файл должен иметь название и не должен быть пустым");
        }

        if (!flagOfErrors) {
            productRepo.save(product);
            return "redirect:/admin";
        }
        return "redirect:/admin/add_product";
    }

    public void deleteProduct(Long id){
        Product product = productRepo.findProductById(id);
        File file = new File(uploadPath + "/" + product.getImageName());
        file.delete();
        productRepo.delete(product);
    }
}
