package com.kushang.jobportal.controller;

import com.kushang.jobportal.dto.LoginRequest;
import com.kushang.jobportal.dto.RegisterRequest;
import com.kushang.jobportal.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Endpoints for user registration and login")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Register a new user",
            description = "Creates a new CANDIDATE or COMPANY account based on the role specified in the request body"
    )

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        String message = authService.registerUser(request);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Authenticate user and return JWT",
            description = "Validates credentials and returns a signed JWT token to be used in the Authorization header for protected endpoints"
    )

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        String token = authService.login(request);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }
}