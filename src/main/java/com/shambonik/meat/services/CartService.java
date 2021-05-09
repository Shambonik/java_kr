package com.shambonik.meat.services;

import com.shambonik.meat.models.Product;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CartService {
    private final ProductService productService;
    @Value("${cookie.name}")
    private String cookieName;

    public String addToCart(String meatCart, long id, HttpServletResponse response){
        if(meatCart!=null){
            StringBuilder stringBuilder = new StringBuilder(meatCart);
            stringBuilder.append(".").append(id);
            meatCart = stringBuilder.toString();
        }
        else{
            meatCart = Long.toString(id);
        }
        Cookie cookie = new Cookie(cookieName, meatCart);
        cookie.setPath("/");//устанавливаем путь
        cookie.setMaxAge(86400);//здесь устанавливается время жизни куки
        response.addCookie(cookie);//добавляем Cookie в запрос
        return "redirect:/";
    }

    public int getCartSize(String meatCart){
        String[] stringIDs = meatCart.split("\\.");
        return stringIDs.length;
    }

    public String getCart(String meatCart, Model model){
        @Data
        class Pair{
            private Product product;
            private long count;

            public Pair(Product product, long count) {
                this.product = product;
                this.count = count;
            }
        }
        String[] stringIDs = meatCart.split("\\.");
        ArrayList<Long> IDs = new ArrayList<>();
        for(String id : stringIDs){
            IDs.add(Long.parseLong(id));
        }
        Map<Long, Integer> mapIDs = new HashMap<>();
        for (int i = 0; i < IDs.size(); i++) {
            Long temp = IDs.get(i);
            if (!mapIDs.containsKey(temp)) {
                mapIDs.put(temp, 1);
            } else {
                mapIDs.put(temp, mapIDs.get(temp) + 1);
            }
        }
        ArrayList<Pair> list = new ArrayList<>();
        for(Map.Entry<Long, Integer> entry : mapIDs.entrySet()){
            Product product = productService.getProductById(entry.getKey());
            if(product != null) {
                list.add(new Pair(product, entry.getValue()));
            }
        }
        model.addAttribute("list", list);
        return "cart";
    }
}
