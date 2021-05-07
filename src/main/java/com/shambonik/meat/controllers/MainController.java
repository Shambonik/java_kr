package com.shambonik.meat.controllers;

import com.shambonik.meat.models.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class MainController {

    @GetMapping
    public String getPage(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("authorized", auth.getPrincipal()!="anonymousUser");
        model.addAttribute("admin", auth.getAuthorities().contains(Role.ADMIN));
        return "index";
    }
}
