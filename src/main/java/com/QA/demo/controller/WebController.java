package com.QA.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebController {

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "User Registration Page - Use POST /api/users to create users";
    }

    @GetMapping("/users")
    public String showUsersList() {
        return "Users List Page - Use GET /api/users to get all users";
    }
}