package com.klef.ms.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import com.klef.ms.dto.LoginRequest;
import com.klef.ms.dto.OrderResponse;
import com.klef.ms.dto.ProductResponse;
import com.klef.ms.dto.UserRequest;
import com.klef.ms.dto.UserResponse;

public interface UserService 
{

    UserResponse saveUser(UserRequest request); // registration

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

    UserResponse updateUser(Long id, UserRequest request);

    void deleteUser(Long id);
    
    //implementation from ProductService Project
    List<ProductResponse> displayAllProducts();
    
    //implementation from OrderService Project
    List<OrderResponse> displayOrdersByUserId(Long userid);
    
    UserResponse userLogin(LoginRequest request);

	UserDetails loadUserByUsername(String username);
}