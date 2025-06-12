package com.tsu.mealtracker.controller;


import com.tsu.mealtracker.dto.RegisterForm;
import com.tsu.mealtracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import org.springframework.ui.Model;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@RequestMapping("/register")
public class RegisterController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> register(@RequestBody @Valid RegisterForm registerForm, BindingResult result) {


        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(e -> e.getField() + ": " + e.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }


        if (userService.existsUsername(registerForm.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Username is already taken.");
        }


        userService.register(registerForm.getUsername(), registerForm.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
    }
}

