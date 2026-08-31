package com.klef.ms.dto;

import lombok.*;

@Data
@Builder
public class UserResponse 
{
    private Long id;
    private String name;
    private String email;
    private String contact;
    private String role;
    private String token;
}