package com.klef.ms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.klef.ms.dto.LoginRequest;
import com.klef.ms.dto.OrderResponse;
import com.klef.ms.dto.ProductResponse;
import com.klef.ms.dto.UserRequest;
import com.klef.ms.dto.UserResponse;
import com.klef.ms.service.UserService;

import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/user")
public class UserController 
{
   @Autowired	
   private UserService service;
	
   @GetMapping("/")
   public String home()
   {
	   return "User Service Project";
   }   
   
       @PostMapping("/add")
       public ResponseEntity<UserResponse> saveUser(@Valid @RequestBody UserRequest request) 
       {
           return new ResponseEntity<>(service.saveUser(request), HttpStatus.CREATED);
       }

       @GetMapping("displayall")
       public ResponseEntity<List<UserResponse>> getAllUsers() 
       {
           return ResponseEntity.ok(service.getAllUsers());
       }

       @GetMapping("display/{id}")
       public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) 
       {
           return ResponseEntity.ok(service.getUserById(id));
       }

       @PutMapping("update/{id}")
       public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,@Valid @RequestBody UserRequest request) 
       {
           return ResponseEntity.ok( service.updateUser(id, request));
       }

       @DeleteMapping("delete/{id}")
       public ResponseEntity<String> deleteUser(@PathVariable Long id) 
       {
           service.deleteUser(id);

           return ResponseEntity.ok("User deleted successfully.");
       }
       
       @GetMapping("/allproducts")
       public ResponseEntity<List<ProductResponse>> displayallProducts()
       {
    	   List<ProductResponse> products = service.displayAllProducts();
    	   
    	   return ResponseEntity.ok(products);
       }
       
       @GetMapping("/displayordersbyuserid")
       public ResponseEntity<List<OrderResponse>> displayordersbyuserid(@RequestParam Long userid)
       {
    	   return ResponseEntity.ok(service.displayOrdersByUserId(userid));
       }
       
       
       @PostMapping("/login")
       public ResponseEntity<UserResponse> userlogin(@Valid @RequestBody LoginRequest request)
       {
    	   UserResponse response = service.userLogin(request);
    	
    	   return ResponseEntity.ok(response); // 200
       }
       
       
      
} 
