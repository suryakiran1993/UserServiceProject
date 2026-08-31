package com.klef.ms.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.klef.ms.client.OrderClient;
import com.klef.ms.client.ProductClient;
import com.klef.ms.dto.LoginRequest;
import com.klef.ms.dto.OrderResponse;
import com.klef.ms.dto.ProductResponse;
import com.klef.ms.dto.UserRequest;
import com.klef.ms.dto.UserResponse;
import com.klef.ms.entity.Role;
import com.klef.ms.entity.User;
import com.klef.ms.exception.ResourceNotFoundException;
import com.klef.ms.exception.UnauthorizedException;
import com.klef.ms.repository.UserRepository;
import com.klef.ms.security.JwtUtil;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService 
{
	@Autowired
	private UserRepository repository;
	
	private final ProductClient productclient;
	
	private final OrderClient orderclient;
	
    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    @Override
    public UserResponse saveUser(UserRequest request) 
    {
        Role userRole;

        try {
            userRole = Role.valueOf(request.getRole().trim().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Role must be one of ADMIN, MANAGER, USER");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .contact(request.getContact())
                .role(userRole)
                .build();

        User savedUser = repository.save(user);

        return mapToResponse(savedUser);
    }

    @Override
    public List<UserResponse> getAllUsers() 
    {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Long id) 
    {

        User user = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id : " + id));

        return mapToResponse(user);
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest request) 
    {

        User user = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id : " + id));

        Role userRole;

        try {
            userRole = Role.valueOf(request.getRole().trim().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Role must be one of ADMIN, MANAGER, USER");
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setContact(request.getContact());
        user.setRole(userRole);

        User updatedUser = repository.save(user);

        return mapToResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {

        User user = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id : " + id));

        repository.delete(user);
    }

    private UserResponse mapToResponse(User user) 
    {
        return mapToResponse(user, null);
    }

    private UserResponse mapToResponse(User user, String token) 
    {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .contact(user.getContact())
                .role(user.getRole().name())
                .token(token)
                .build();
    }

    
    @Override
    @CircuitBreaker(
            name = "ProductService",
            fallbackMethod = "ProductServiceFallback"
    )
    public List<ProductResponse> displayAllProducts() 
    {
        List<ProductResponse> products = productclient.getAllProducts();

        System.out.println("Product data received successfully:");
        System.out.println(products);

        return products;
    }
    
    public List<ProductResponse> ProductServiceFallback(Throwable throwable) 
    {
        System.out.println("================================");
        System.out.println("Product Service is unavailable!");
        System.out.println("Circuit Breaker Fallback executed.");
        System.out.println("================================");

        return Collections.emptyList();
    }
	

	@Override
	public List<OrderResponse> displayOrdersByUserId(Long userid) 
	{
		return orderclient.displayordersbyuserid(userid);
		
	}

	@Override
	public UserResponse userLogin(LoginRequest request) 
	{
        User user = repository.findByEmail(request.getEmail())
                .filter(foundUser -> passwordEncoder.matches(
                        request.getPassword(),
                        foundUser.getPassword()))
                .orElseThrow(() ->
                        new UnauthorizedException(
                                "Invalid Email or Password"));

        UserDetails userDetails = loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        return mapToResponse(user, token);
    }

	@Override
	public UserDetails loadUserByUsername(String username)
	{
            User user = repository.findByEmail(username)
	            .orElseThrow(() ->
	                    new UsernameNotFoundException(
	                            "User not found with email: " + username));

	    return org.springframework.security.core.userdetails.User
	            .withUsername(user.getEmail())
	            .password(user.getPassword())
	            .authorities(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
	            .build();
	}
}
