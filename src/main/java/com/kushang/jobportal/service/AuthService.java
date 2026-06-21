package com.kushang.jobportal.service;

import com.kushang.jobportal.entity.User;
import com.kushang.jobportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.kushang.jobportal.dto.RegisterRequest;
import com.kushang.jobportal.dto.LoginRequest;
import com.kushang.jobportal.entity.Role;
import com.kushang.jobportal.security.JwtUtil;
import com.kushang.jobportal.exception.InvalidCredentialsException;
import com.kushang.jobportal.exception.UserAlreadyExistsException;
import com.kushang.jobportal.exception.AdminRegistrationException;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered!");

        }

        if (request.getRole() == Role.ADMIN) {
            throw new AdminRegistrationException("Self-registration as ADMIN is not allowed!");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        userRepository.save(user);
        return "User registered successfully!";
    }
    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return jwtUtil.generateToken(user.getEmail(), user.getRole().name());
    }

}