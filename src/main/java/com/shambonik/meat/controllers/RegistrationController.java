package com.shambonik.meat.controllers;

import com.shambonik.meat.models.Role;
import com.shambonik.meat.models.User;
import com.shambonik.meat.repositories.UserRepo;
import com.shambonik.meat.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.Map;

@Controller
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationController {
    private final UserService userService;

    @GetMapping
    public String getPage(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getPrincipal()!="anonymousUser"){
            return "redirect:/";
        }
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping()
    public String addUser(User user, Map<String, Object> model){
        if(!userService.addUser(user)){
            model.put("message", "User exists!");
            return "/registration";
        }
        return "redirect:/login";
    }
}
