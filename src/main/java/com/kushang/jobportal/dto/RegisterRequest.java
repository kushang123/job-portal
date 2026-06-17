package com.kushang.jobportal.dto;

import com.kushang.jobportal.entity.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private Role role;
}