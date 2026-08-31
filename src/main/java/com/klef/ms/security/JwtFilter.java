package com.klef.ms.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.klef.ms.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter 
{
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService service;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // Public endpoints
        
        if (path.startsWith("/user/login")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")) 
        {

            chain.doFilter(request, response);
            return;
        }

        // Read Authorization header
        
        String header = request.getHeader("Authorization");

        System.out.println("Authorization Header = " + header);

        // Check Bearer token
        if (header == null || !header.startsWith("Bearer ")) 
        {
            sendErrorResponse(
                    response,
                    401,
                    "Authorization header is missing or invalid"
            );

            return;
        }

        String token = header.substring(7).trim();

        try {

            // Extract username/email from token
            String username = jwtUtil.extractUsername(token);

            System.out.println(
                    "Username from JWT = " + username
            );

            // Check whether authentication already exists
            if (SecurityContextHolder
                    .getContext()
                    .getAuthentication() == null) {

                // Load user from database
                UserDetails userDetails =
                        service.loadUserByUsername(username);

                // Validate JWT
                if (userDetails != null
                        && jwtUtil.validateToken(
                                token,
                                userDetails)) {

                    // Create authentication object
                    UsernamePasswordAuthenticationToken
                            authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    // Add request details
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    // Store authentication
                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authToken);

                    System.out.println(
                            "JWT validation successful"
                    );

                } else {

                    sendErrorResponse(
                            response,
                            401,
                            "Invalid or expired token"
                    );

                    return;
                }
            }

        } catch (Exception e) {

            sendErrorResponse(
                    response,
                    401,
                    "Invalid token"
            );

            return;
        }

        // Continue request
        chain.doFilter(request, response);
    }

    private void sendErrorResponse(
            HttpServletResponse response,
            int status,
            String message)
            throws IOException {

        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse =
                "{\"error\":\"Unauthorized\",\"message\":\""
                + message
                + "\"}";

        response.getWriter().write(jsonResponse);
    }
}