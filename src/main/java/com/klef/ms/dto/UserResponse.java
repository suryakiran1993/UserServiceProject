package com.klef.ms.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String token;
}