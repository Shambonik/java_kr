package com.shambonik.meat.services;

import com.shambonik.meat.models.Product;
import com.shambonik.meat.models.ProductCount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CartService {
    private final ProductService productService;
    @Value("${cookie.cart.name}")
    private String cookieName;

    @Data
    @AllArgsConstructor
    public static class Pair{
        private List<ProductCount> productCounts;
         private int totalPrice;
    }

    public Pair getProducts(String meatCart){
        if(meatCart!=null && !meatCart.equals("")) {
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
            ArrayList<ProductCount> list = new ArrayList<>();
            int totalPrice = 0;
            for (Map.Entry<Long, Integer> entry : mapIDs.entrySet()) {
                Product product = productService.getProductById(entry.getKey());
                if (product != null && product.isActive()) {
                    list.add(new ProductCount(product, entry.getValue()));
                    totalPrice += product.getPrice() * entry.getValue();
                }
            }
            return new Pair(list, totalPrice);
        }
        return new Pair(null, 0);
    }

    public String addToCart(String meatCart, long id, HttpServletResponse response){
        Cookie cookie = new Cookie(cookieName,
                (meatCart != null ? meatCart : "") + id + ".");
        cookie.setPath("/");
        cookie.setMaxAge(86400);
        response.addCookie(cookie);
        return "redirect:/";
    }

    private String[] getListOfIDs(String meatCart){
        return meatCart.split("\\.");
    }

    public int getCartSize(String meatCart, HttpServletResponse response){
        if(meatCart!=null && !meatCart.equals("")) {
            boolean changes = false;
            List<String> stringIDs = Arrays.asList(getListOfIDs(meatCart));
            Map<Long, Integer> mapIDs = new HashMap<>();
            for (String stringID : stringIDs) {
                Long temp = Long.parseLong(stringID);
                Product product = productService.getProductById(temp);
                if (!mapIDs.containsKey(temp)) {
                    if (product != null && product.isActive())
                        mapIDs.put(temp, 1);
                    else
                        changes = true;
                } else {
                    if (product.getCount() > mapIDs.get(temp))
                        mapIDs.put(temp, mapIDs.get(temp) + 1);
                    else
                        changes = true;
                }
            }
            if(changes){
                StringBuilder stringBuilder = new StringBuilder();
                int count = 0;
                for(Map.Entry<Long, Integer> entry : mapIDs.entrySet()){
                    for(int i = 0; i < entry.getValue(); i++){
                        stringBuilder.append(entry.getKey()).append(".");
                        count++;
                    }
                }
                Cookie cookie = new Cookie(cookieName, stringBuilder.toString());
                cookie.setPath("/");
                cookie.setMaxAge(86400);
                response.addCookie(cookie);
                return count;
            }
            return stringIDs.size();
        }
        return 0;
    }

    public String getCart(String meatCart, Model model){
        Pair pair = getProducts(meatCart);
        model.addAttribute("list", pair.getProductCounts());
        model.addAttribute("totalPrice", pair.getTotalPrice());
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
        Cookie cookie = new Cookie(cookieName, stringBuilder.toString());
        cookie.setPath("/");
        cookie.setMaxAge(86400);
        response.addCookie(cookie);
        return "redirect:/cart";
    }

    public void clearCart(HttpServletResponse response){
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setPath("/");
        cookie.setMaxAge(86400);
        response.addCookie(cookie);
    }
}
