package com.evcharging.controller;

import com.evcharging.dto.RegisterRequest;
import com.evcharging.exception.DuplicateEmailException;
import com.evcharging.security.SecurityUtils;
import com.evcharging.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            @RequestParam(value = "registered", required = false) String registered,
                            Model model) {
        if (SecurityUtils.isAuthenticated()) {
            if (SecurityUtils.isAdmin()) {
                return "redirect:/admin/dashboard";
            }
            return "redirect:/user/dashboard";
        }

        if (error != null) {
            model.addAttribute("errorMessage", "Invalid email or password. Please try again.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out successfully.");
        }
        if (registered != null) {
            model.addAttribute("successMessage", "Registration successful! You may now sign in.");
        }

        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        if (SecurityUtils.isAuthenticated()) {
            return "redirect:/user/dashboard";
        }
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                                      BindingResult bindingResult,
                                      Model model) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.registerRequest", "Passwords do not match");
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            userService.registerUser(request);
            return "redirect:/login?registered=true";
        } catch (DuplicateEmailException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Registration failed: " + e.getMessage());
            return "register";
        }
    }
}
