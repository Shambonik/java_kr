package com.shambonik.meat.controllers;

import com.shambonik.meat.models.User;
import com.shambonik.meat.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/edit_account")
@RequiredArgsConstructor
public class EditAccountController {
    private final UserService userService;

    @GetMapping
    public String getPage(@AuthenticationPrincipal User user, Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(auth);
        model.addAttribute("user", auth.getPrincipal());
        return "edit_account";
    }

    @PostMapping()
    public String editUser(@AuthenticationPrincipal User originalUser, User user){
        userService.saveUser(originalUser, user);
        return "redirect:/login";
    }
}
