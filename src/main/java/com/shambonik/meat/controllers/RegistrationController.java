package com.shambonik.meat.controllers;

import com.shambonik.meat.models.Role;
import com.shambonik.meat.models.User;
import com.shambonik.meat.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Controller
@RequestMapping("/registration")
public class RegistrationController {
    private final UserRepo userRepo;

    public RegistrationController(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

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
        User userFromDb = userRepo.findByUsername(user.getUsername());
        if(userFromDb!=null){
            model.put("message", "User exists!");
            return "/registration";
        }
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        userRepo.save(user);
        return "redirect:/login";
    }
}
