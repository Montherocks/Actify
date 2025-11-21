package com.actify.controller;

import com.actify.model.User;
import com.actify.repository.UserRepository;
import com.actify.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> request) {
        try {
            User user = new User();
            
            // Handle name field - split into firstName and lastName
            String name = (String) request.get("name");
            if (name != null && !name.trim().isEmpty()) {
                String[] nameParts = name.trim().split("\\s+", 2);
                user.setFirstName(nameParts[0]);
                user.setLastName(nameParts.length > 1 ? nameParts[1] : nameParts[0]);
            } else {
                user.setFirstName("User");
                user.setLastName("User");
            }
            
            user.setEmail((String) request.get("email"));
            user.setPassword(passwordEncoder.encode((String) request.get("password")));
            
            // Set userType from request or default to volunteer
            String userType = (String) request.get("userType");
            user.setUserType(userType != null ? userType : "volunteer");
            
            // Set default values for required fields
            user.setPhone(request.get("phone") != null ? (String) request.get("phone") : "Not provided");
            user.setCountry(request.get("country") != null ? (String) request.get("country") : "Not provided");
            user.setCity(request.get("city") != null ? (String) request.get("city") : "Not provided");
            user.setNeighborhood(request.get("neighborhood") != null ? (String) request.get("neighborhood") : "Not provided");
            user.setInterests(request.get("interests") != null ? (String) request.get("interests") : "");
            
            userRepository.save(user);
            
            // Generate token for auto-login
            String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User registered successfully");
            response.put("token", token);
            response.put("userId", user.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if(!userOpt.isPresent() || !passwordEncoder.matches(password, userOpt.get().getPassword())) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Invalid email or password");
            return ResponseEntity.badRequest().body(response);
        }
        
        User user = userOpt.get();
        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getId());
        response.put("success", true);
        return ResponseEntity.ok(response);
    }
}
