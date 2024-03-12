package com.project.spring.jwt.mongodb.controllers;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.spring.jwt.mongodb.models.User;
import com.project.spring.jwt.mongodb.repository.UserRepository;
import com.project.spring.jwt.mongodb.security.jwt.JwtUtils;
import com.project.spring.jwt.mongodb.security.services.UserDetailsImpl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {
	
	@GetMapping("/all")
	public String allAccess() {
		return "Public Content.";
	}
	
	
	
	@Autowired
	JwtUtils jwtUtils;


@GetMapping("/user")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Map<String, String>> userAccess(@RequestHeader("Authorization") String token) {
	    
	    String jwtToken = token.substring(7); 
	    System.out.println(jwtToken);

	    UserDetailsImpl userDetails = jwtUtils.getAllDetailsFromJwtToken(jwtToken);

	    System.out.println(userDetails);
	    
	    Map<String, String> userDetailsMap = new HashMap<>();
	    userDetailsMap.put("id", userDetails.getId());
	    userDetailsMap.put("username", userDetails.getUsername());
	    userDetailsMap.put("email", userDetails.getEmail());
	    userDetailsMap.put("isAuthenticated", String.valueOf(userDetails.isAuthenticated()));

	    return ResponseEntity.ok(userDetailsMap);
	}

	
	






	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
