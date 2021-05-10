package com.shambonik.meat.services;

import com.shambonik.meat.models.Product;
import com.shambonik.meat.Pair;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
        StringBuilder stringBuilder = new StringBuilder(meatCart!=null?meatCart:"");
        stringBuilder.append(id).append(".");
        Cookie cookie = new Cookie(cookieName, stringBuilder.toString());
        cookie.setPath("/");
        cookie.setMaxAge(86400);
        response.addCookie(cookie);
        return "redirect:/";
    }

    private String[] getListOfIDs(String meatCart){
        return meatCart.split("\\.");
    }

    public int getCartSize(String meatCart){
        if(meatCart!=null) {
            String[] stringIDs = getListOfIDs(meatCart);
            return stringIDs.length;
        }
        return 0;
    }

    public String getCart(String meatCart, Model model){
        if(meatCart!=null) {
            String[] stringIDs = getListOfIDs(meatCart);
            ArrayList<Long> IDs = new ArrayList<>();
            for (String id : stringIDs) {
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
            for (Map.Entry<Long, Integer> entry : mapIDs.entrySet()) {
                Product product = productService.getProductById(entry.getKey());
                if (product != null) {
                    list.add(new Pair(product, entry.getValue()));
                }
            }
            model.addAttribute("list", list);
        }
        else {
            model.addAttribute("list", null);
        }
        return "cart";
    }

    public String editProductCount(long id, int count, String meatCart, HttpServletResponse response){
        List<String> stringIDs = Arrays.asList(getListOfIDs(meatCart));
        String strID = Long.toString(id);
        StringBuilder stringBuilder = new StringBuilder();
        for(String str: stringIDs) {
            if(!str.equals(strID)) stringBuilder.append(str).append(".");
        }
        for(int i = 0; i < count; i++) stringBuilder.append(id).append(".");
        System.out.println(stringBuilder.toString());
        Cookie cookie = new Cookie(cookieName, stringBuilder.toString());
        cookie.setPath("/");
        cookie.setMaxAge(86400);
        response.addCookie(cookie);
        return "redirect:/cart";
    }
}
