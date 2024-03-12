package com.project.spring.jwt.mongodb.security.jwt;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.project.spring.jwt.mongodb.models.User;
import com.project.spring.jwt.mongodb.repository.UserRepository;import com.project.spring.jwt.mongodb.security.jwt.JwtUtils;
import com.project.spring.jwt.mongodb.security.services.UserDetailsImpl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
  private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

  @Value("${project.app.jwtSecret}")
  private String jwtSecret;

  @Value("${project.app.jwtExpirationMs}")
  private int jwtExpirationMs;

  public String generateJwtToken(Authentication authentication) {

    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

    return Jwts.builder()
        .setSubject((userPrincipal.getUsername()))
        .setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
        .signWith(key(), SignatureAlgorithm.HS256)
        .compact();
  }
  
  private Key key() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
  }

  public String getUserNameFromJwtToken(String token) {
    return Jwts.parserBuilder().setSigningKey(key()).build()
               .parseClaimsJws(token).getBody().getSubject();
  }
  
  
  

	@Autowired
	  private UserRepository userRepo;


	  public UserDetailsImpl getAllDetailsFromJwtToken(String token) {
		    try {
		        Claims claims = Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody();

		        System.out.println("Claims from JWT: " + claims); 

		        String username = claims.getSubject();

		        Optional <User> myUserOptional = userRepo.findByUsername(username);
		        if (myUserOptional.isEmpty()) {
		            throw new IllegalArgumentException("User not found for username: " + username);
		        }
		        User user = myUserOptional.get();

		        
		        String id = user.getId(); 
		        String email = user.getEmail(); 
		        boolean isAuthenticated = true; 

		        return new UserDetailsImpl(id, username, email, "", isAuthenticated);
		    } catch (JwtException | ClassCastException e) {
		        throw new IllegalArgumentException("Invalid JWT token or missing fields", e);
		    }
		}


  public boolean validateJwtToken(String authToken) {
    try {
      Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
      return true;
    } catch (MalformedJwtException e) {
      logger.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      logger.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      logger.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty: {}", e.getMessage());
    }

    return false;
  }
}
