package com.actify.controller;

import com.actify.model.Admin;
import com.actify.model.Event;
import com.actify.repository.AdminRepository;
import com.actify.repository.EventRepository;
import com.actify.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    // Admin Login
    @PostMapping("/login")
    public ResponseEntity<?> adminLogin(@RequestBody Map<String, String> loginRequest) {
        String identifier = loginRequest.get("username"); // Can be username or email
        String password = loginRequest.get("password");
        
        if (identifier == null || password == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Username and password are required");
            return ResponseEntity.badRequest().body(error);
        }
        
        // Find admin by username or email
        Optional<Admin> adminOpt = adminRepository.findByUsernameOrEmail(identifier, identifier);
        
        if (adminOpt.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Invalid credentials");
            return ResponseEntity.status(401).body(error);
        }
        
        Admin admin = adminOpt.get();
        
        // Check if admin is active
        if (!admin.getIsActive()) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "This admin account has been deactivated");
            return ResponseEntity.status(401).body(error);
        }
        
        // Verify password
        if (!passwordEncoder.matches(password, admin.getPassword())) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Invalid credentials");
            return ResponseEntity.status(401).body(error);
        }
        
        // Update last login
        admin.setLastLogin(LocalDateTime.now());
        adminRepository.save(admin);
        
        // Generate JWT token
        String token = jwtTokenProvider.createToken(admin.getEmail(), "admin");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Login successful");
        response.put("token", token);
        response.put("adminId", admin.getId());
        response.put("username", admin.getUsername());
        response.put("email", admin.getEmail());
        response.put("fullName", admin.getFullName());
        response.put("role", admin.getRole());
        
        return ResponseEntity.ok(response);
    }
    
    // Verify admin token
    @GetMapping("/verify")
    public ResponseEntity<?> verifyAdmin(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("valid", false, "message", "No token provided"));
            }
            
            String token = authHeader.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                String email = jwtTokenProvider.getEmailFromToken(token);
                Optional<Admin> admin = adminRepository.findByEmail(email);
                
                if (admin.isPresent() && admin.get().getIsActive()) {
                    return ResponseEntity.ok(Map.of(
                        "valid", true,
                        "adminId", admin.get().getId(),
                        "username", admin.get().getUsername(),
                        "role", admin.get().getRole()
                    ));
                }
            }
            return ResponseEntity.status(401).body(Map.of("valid", false, "message", "Invalid token"));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("valid", false, "message", "Token validation failed"));
        }
    }
    
    // Create initial admin (only works if no admins exist - for initial setup)
    @PostMapping("/setup")
    public ResponseEntity<?> setupAdmin(@RequestBody Map<String, String> setupRequest) {
        // Check if any admin already exists
        if (adminRepository.count() > 0) {
            // If admins exist, require super_admin token to create new admin
            String authHeader = setupRequest.get("authToken");
            if (authHeader == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Admin system already initialized. Contact super admin to add new admins.");
                return ResponseEntity.status(403).body(error);
            }
        }
        
        String username = setupRequest.get("username");
        String email = setupRequest.get("email");
        String password = setupRequest.get("password");
        String fullName = setupRequest.get("fullName");
        String secretKey = setupRequest.get("secretKey");
        
        // Validate secret key for initial setup (change this in production!)
        String ADMIN_SECRET_KEY = "ACTIFY_ADMIN_2024_SECRET";
        if (!ADMIN_SECRET_KEY.equals(secretKey)) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Invalid setup key");
            return ResponseEntity.status(403).body(error);
        }
        
        if (username == null || email == null || password == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Username, email, and password are required");
            return ResponseEntity.badRequest().body(error);
        }
        
        // Check if username or email already exists
        if (adminRepository.existsByUsername(username)) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Username already exists"));
        }
        if (adminRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Email already exists"));
        }
        
        // Create new admin
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setFullName(fullName != null ? fullName : username);
        admin.setRole(adminRepository.count() == 0 ? "super_admin" : "admin");
        admin.setIsActive(true);
        
        adminRepository.save(admin);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Admin created successfully");
        response.put("adminId", admin.getId());
        response.put("username", admin.getUsername());
        response.put("role", admin.getRole());
        
        return ResponseEntity.ok(response);
    }
    
    // Get all pending events
    @GetMapping("/events/pending")
    public ResponseEntity<?> getPendingEvents() {
        List<Event> events = eventRepository.findByStatus("pending");
        return ResponseEntity.ok(events);
    }
    
    // Approve an event
    @PutMapping("/events/{id}/approve")
    public ResponseEntity<?> approveEvent(@PathVariable Long id) {
        Optional<Event> eventOpt = eventRepository.findById(id);
        
        if (eventOpt.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Event not found");
            return ResponseEntity.notFound().build();
        }
        
        Event event = eventOpt.get();
        event.setStatus("active");
        eventRepository.save(event);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Event approved successfully");
        response.put("event", event);
        
        return ResponseEntity.ok(response);
    }
    
    // Reject an event
    @PutMapping("/events/{id}/reject")
    public ResponseEntity<?> rejectEvent(@PathVariable Long id) {
        Optional<Event> eventOpt = eventRepository.findById(id);
        
        if (eventOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Event event = eventOpt.get();
        event.setStatus("rejected");
        eventRepository.save(event);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Event rejected");
        response.put("event", event);
        
        return ResponseEntity.ok(response);
    }
    
    // Update event status
    @PutMapping("/events/{id}/status")
    public ResponseEntity<?> updateEventStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Optional<Event> eventOpt = eventRepository.findById(id);
        
        if (eventOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        String newStatus = body.get("status");
        if (newStatus == null || newStatus.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Status is required");
            return ResponseEntity.badRequest().body(error);
        }
        
        Event event = eventOpt.get();
        event.setStatus(newStatus);
        eventRepository.save(event);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Event status updated to " + newStatus);
        response.put("event", event);
        
        return ResponseEntity.ok(response);
    }
    
    // Get admin stats
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        List<Event> allEvents = eventRepository.findAll();
        
        long pendingCount = allEvents.stream().filter(e -> "pending".equals(e.getStatus())).count();
        long activeCount = allEvents.stream().filter(e -> "active".equals(e.getStatus())).count();
        long rejectedCount = allEvents.stream().filter(e -> "rejected".equals(e.getStatus())).count();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEvents", allEvents.size());
        stats.put("pendingEvents", pendingCount);
        stats.put("activeEvents", activeCount);
        stats.put("rejectedEvents", rejectedCount);
        
        return ResponseEntity.ok(stats);
    }
}
