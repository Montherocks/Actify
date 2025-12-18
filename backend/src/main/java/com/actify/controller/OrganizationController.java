package com.actify.controller;

import com.actify.model.Organization;
import com.actify.model.Event;
import com.actify.model.EventRegistration;
import com.actify.model.User;
import com.actify.repository.OrganizationRepository;
import com.actify.repository.EventRepository;
import com.actify.repository.EventRegistrationRepository;
import com.actify.repository.UserRepository;
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
import java.util.ArrayList;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/org")
public class OrganizationController {
    
    @Autowired
    private OrganizationRepository organizationRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private EventRegistrationRepository eventRegistrationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    // ===== Authentication Endpoints =====
    
    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> request) {
        try {
            String email = (String) request.get("email");
            
            // Check if email already exists
            if (organizationRepository.existsByEmail(email)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Email already registered");
                return ResponseEntity.badRequest().body(response);
            }
            
            Organization org = new Organization();
            org.setName((String) request.get("name"));
            org.setEmail(email);
            org.setPassword(passwordEncoder.encode((String) request.get("password")));
            org.setOrgType((String) request.get("orgType"));
            org.setDescription((String) request.get("description"));
            org.setWebsite((String) request.get("website"));
            org.setContactName((String) request.get("contactName"));
            org.setPhone((String) request.get("phone"));
            org.setAddress((String) request.get("address"));
            org.setCity((String) request.get("city"));
            org.setState((String) request.get("state"));
            
            organizationRepository.save(org);
            
            String token = jwtTokenProvider.generateToken(org.getId(), org.getEmail());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Organization registered successfully");
            response.put("token", token);
            response.put("orgId", org.getId());
            response.put("name", org.getName());
            response.put("email", org.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        
        Optional<Organization> orgOpt = organizationRepository.findByEmail(email);
        
        if (!orgOpt.isPresent() || !passwordEncoder.matches(password, orgOpt.get().getPassword())) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Invalid email or password");
            return ResponseEntity.badRequest().body(response);
        }
        
        Organization org = orgOpt.get();
        
        if (!org.getActive()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "This organization account is deactivated");
            return ResponseEntity.badRequest().body(response);
        }
        
        String token = jwtTokenProvider.generateToken(org.getId(), org.getEmail());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("token", token);
        response.put("orgId", org.getId());
        response.put("name", org.getName());
        response.put("email", org.getEmail());
        response.put("orgType", org.getOrgType());
        response.put("verified", org.getVerified());
        return ResponseEntity.ok(response);
    }
    
    // ===== Dashboard & Stats Endpoints =====
    
    @GetMapping("/stats")
    public ResponseEntity<?> getOrgStats(@RequestHeader("Authorization") String authHeader) {
        try {
            Long orgId = extractOrgIdFromToken(authHeader);
            Optional<Organization> orgOpt = organizationRepository.findById(orgId);
            
            if (!orgOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Organization org = orgOpt.get();
            
            // Get events count for this organization
            List<Event> events = eventRepository.findByOrganizerId(orgId);
            int activeEvents = (int) events.stream().filter(e -> "active".equals(e.getStatus())).count();
            int completedEvents = (int) events.stream().filter(e -> "completed".equals(e.getStatus())).count();
            int pendingEvents = (int) events.stream().filter(e -> "pending".equals(e.getStatus())).count();
            
            // Calculate total volunteers registered across all events
            int totalVolunteers = 0;
            int totalVpDistributed = 0;
            for (Event event : events) {
                totalVolunteers += event.getVolunteersRegistered() != null ? event.getVolunteersRegistered() : 0;
                // Calculate VP distributed for active/completed events
                if ("active".equals(event.getStatus()) || "completed".equals(event.getStatus())) {
                    int eventVolunteers = event.getVolunteersRegistered() != null ? event.getVolunteersRegistered() : 0;
                    totalVpDistributed += eventVolunteers * (event.getPointsReward() != null ? event.getPointsReward() : 0);
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("totalEvents", events.size());
            response.put("activeEvents", activeEvents);
            response.put("completedEvents", completedEvents);
            response.put("volunteersReached", totalVolunteers);
            response.put("vpDistributed", totalVpDistributed);
            response.put("pendingEvents", pendingEvents);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/profile")
    public ResponseEntity<?> getOrgProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            Long orgId = extractOrgIdFromToken(authHeader);
            Optional<Organization> orgOpt = organizationRepository.findById(orgId);
            
            if (!orgOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Organization org = orgOpt.get();
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", org.getId());
            response.put("name", org.getName());
            response.put("email", org.getEmail());
            response.put("orgType", org.getOrgType());
            response.put("description", org.getDescription());
            response.put("website", org.getWebsite());
            response.put("contactName", org.getContactName());
            response.put("phone", org.getPhone());
            response.put("address", org.getAddress());
            response.put("city", org.getCity());
            response.put("state", org.getState());
            response.put("logoUrl", org.getLogoUrl());
            response.put("verified", org.getVerified());
            response.put("totalEvents", org.getTotalEvents());
            response.put("volunteersReached", org.getVolunteersReached());
            response.put("vpDistributed", org.getVpDistributed());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/profile")
    public ResponseEntity<?> updateOrgProfile(@RequestHeader("Authorization") String authHeader, 
                                              @RequestBody Map<String, Object> request) {
        try {
            Long orgId = extractOrgIdFromToken(authHeader);
            Optional<Organization> orgOpt = organizationRepository.findById(orgId);
            
            if (!orgOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Organization org = orgOpt.get();
            
            if (request.containsKey("name")) org.setName((String) request.get("name"));
            if (request.containsKey("description")) org.setDescription((String) request.get("description"));
            if (request.containsKey("website")) org.setWebsite((String) request.get("website"));
            if (request.containsKey("contactName")) org.setContactName((String) request.get("contactName"));
            if (request.containsKey("phone")) org.setPhone((String) request.get("phone"));
            if (request.containsKey("address")) org.setAddress((String) request.get("address"));
            if (request.containsKey("city")) org.setCity((String) request.get("city"));
            if (request.containsKey("state")) org.setState((String) request.get("state"));
            if (request.containsKey("logoUrl")) org.setLogoUrl((String) request.get("logoUrl"));
            
            org.setUpdatedAt(LocalDateTime.now());
            organizationRepository.save(org);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Profile updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // ===== Event Management Endpoints =====
    
    @GetMapping("/events")
    public ResponseEntity<?> getOrgEvents(@RequestHeader("Authorization") String authHeader,
                                          @RequestParam(required = false) String status) {
        try {
            Long orgId = extractOrgIdFromToken(authHeader);
            List<Event> events = eventRepository.findByOrganizerId(orgId);
            
            // Filter by status if provided
            if (status != null && !status.isEmpty() && !"all".equals(status)) {
                events = events.stream()
                    .filter(e -> status.equals(e.getStatus()))
                    .collect(Collectors.toList());
            }
            
            // Convert to response format
            List<Map<String, Object>> eventList = events.stream().map(event -> {
                Map<String, Object> eventMap = new HashMap<>();
                eventMap.put("id", event.getId());
                eventMap.put("title", event.getTitle());
                eventMap.put("description", event.getDescription());
                eventMap.put("date", event.getDate());
                eventMap.put("time", event.getTime());
                eventMap.put("location", event.getLocation());
                eventMap.put("city", event.getCity());
                eventMap.put("cause", event.getCause());
                eventMap.put("capacity", event.getCapacity());
                eventMap.put("volunteersRegistered", event.getVolunteersRegistered());
                eventMap.put("pointsReward", event.getPointsReward());
                eventMap.put("status", event.getStatus());
                eventMap.put("imageUrl", event.getImageUrl());
                return eventMap;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(eventList);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/events")
    public ResponseEntity<?> createEvent(@RequestHeader("Authorization") String authHeader,
                                         @RequestBody Map<String, Object> request) {
        try {
            Long orgId = extractOrgIdFromToken(authHeader);
            Optional<Organization> orgOpt = organizationRepository.findById(orgId);
            
            if (!orgOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Organization org = orgOpt.get();
            
            Event event = new Event();
            event.setTitle((String) request.get("title"));
            event.setDescription((String) request.get("description"));
            event.setDate((String) request.get("date"));
            event.setTime((String) request.get("time"));
            event.setEndTime((String) request.get("endTime"));
            event.setDuration(request.get("duration") != null ? ((Number) request.get("duration")).intValue() : 0);
            event.setLocation((String) request.get("location"));
            event.setCity((String) request.get("city"));
            event.setState((String) request.get("state"));
            
            // Set latitude and longitude for map display
            if (request.get("latitude") != null) {
                event.setLatitude(((Number) request.get("latitude")).doubleValue());
            }
            if (request.get("longitude") != null) {
                event.setLongitude(((Number) request.get("longitude")).doubleValue());
            }
            
            event.setCause((String) request.get("cause"));
            event.setCapacity(request.get("capacity") != null ? ((Number) request.get("capacity")).intValue() : 50);
            event.setPointsReward(request.get("pointsReward") != null ? ((Number) request.get("pointsReward")).intValue() : 100);
            event.setSkillsNeeded((String) request.get("skillsNeeded"));
            event.setRequirements((String) request.get("requirements"));
            event.setContactEmail((String) request.get("contactEmail"));
            event.setContactPhone((String) request.get("contactPhone"));
            event.setOrganizerId(orgId);
            event.setOrganizerName(org.getName());
            event.setStatus("pending"); // Events need admin approval
            event.setVolunteersRegistered(0);
            
            if (request.containsKey("imageUrl")) {
                event.setImageUrl((String) request.get("imageUrl"));
            }
            
            eventRepository.save(event);
            
            // Update organization stats
            org.setTotalEvents(org.getTotalEvents() + 1);
            organizationRepository.save(org);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Event created successfully. It will be visible after admin approval.");
            response.put("eventId", event.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to create event: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/events/{eventId}")
    public ResponseEntity<?> getEventDetails(@RequestHeader("Authorization") String authHeader,
                                             @PathVariable Long eventId) {
        try {
            Long orgId = extractOrgIdFromToken(authHeader);
            Optional<Event> eventOpt = eventRepository.findById(eventId);
            
            if (!eventOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Event event = eventOpt.get();
            
            // Check if this event belongs to the organization
            if (!orgId.equals(event.getOrganizerId())) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Unauthorized access to event");
                return ResponseEntity.status(403).body(response);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", event.getId());
            response.put("title", event.getTitle());
            response.put("description", event.getDescription());
            response.put("date", event.getDate());
            response.put("time", event.getTime());
            response.put("endTime", event.getEndTime());
            response.put("duration", event.getDuration());
            response.put("location", event.getLocation());
            response.put("city", event.getCity());
            response.put("state", event.getState());
            response.put("cause", event.getCause());
            response.put("capacity", event.getCapacity());
            response.put("volunteersRegistered", event.getVolunteersRegistered());
            response.put("pointsReward", event.getPointsReward());
            response.put("skillsNeeded", event.getSkillsNeeded());
            response.put("requirements", event.getRequirements());
            response.put("contactEmail", event.getContactEmail());
            response.put("contactPhone", event.getContactPhone());
            response.put("status", event.getStatus());
            response.put("imageUrl", event.getImageUrl());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/events/{eventId}")
    public ResponseEntity<?> updateEvent(@RequestHeader("Authorization") String authHeader,
                                         @PathVariable Long eventId,
                                         @RequestBody Map<String, Object> request) {
        try {
            Long orgId = extractOrgIdFromToken(authHeader);
            Optional<Event> eventOpt = eventRepository.findById(eventId);
            
            if (!eventOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Event event = eventOpt.get();
            
            // Check if this event belongs to the organization
            if (!orgId.equals(event.getOrganizerId())) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Unauthorized access to event");
                return ResponseEntity.status(403).body(response);
            }
            
            // Update fields
            if (request.containsKey("title")) event.setTitle((String) request.get("title"));
            if (request.containsKey("description")) event.setDescription((String) request.get("description"));
            if (request.containsKey("date")) event.setDate((String) request.get("date"));
            if (request.containsKey("time")) event.setTime((String) request.get("time"));
            if (request.containsKey("endTime")) event.setEndTime((String) request.get("endTime"));
            if (request.containsKey("location")) event.setLocation((String) request.get("location"));
            if (request.containsKey("city")) event.setCity((String) request.get("city"));
            if (request.containsKey("state")) event.setState((String) request.get("state"));
            if (request.containsKey("cause")) event.setCause((String) request.get("cause"));
            if (request.containsKey("capacity")) event.setCapacity(((Number) request.get("capacity")).intValue());
            if (request.containsKey("pointsReward")) event.setPointsReward(((Number) request.get("pointsReward")).intValue());
            if (request.containsKey("status")) event.setStatus((String) request.get("status"));
            
            eventRepository.save(event);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Event updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<?> deleteEvent(@RequestHeader("Authorization") String authHeader,
                                         @PathVariable Long eventId) {
        try {
            Long orgId = extractOrgIdFromToken(authHeader);
            Optional<Event> eventOpt = eventRepository.findById(eventId);
            
            if (!eventOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Event event = eventOpt.get();
            
            // Check if this event belongs to the organization
            if (!orgId.equals(event.getOrganizerId())) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Unauthorized access to event");
                return ResponseEntity.status(403).body(response);
            }
            
            eventRepository.delete(event);
            
            // Update organization stats
            Optional<Organization> orgOpt = organizationRepository.findById(orgId);
            if (orgOpt.isPresent()) {
                Organization org = orgOpt.get();
                org.setTotalEvents(Math.max(0, org.getTotalEvents() - 1));
                organizationRepository.save(org);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Event deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // ===== Volunteer Management Endpoints =====
    
    @GetMapping("/volunteers")
    public ResponseEntity<?> getOrgVolunteers(@RequestHeader("Authorization") String authHeader) {
        try {
            Long orgId = extractOrgIdFromToken(authHeader);
            
            // Get all events for this organization
            List<Event> orgEvents = eventRepository.findByOrganizerId(orgId);
            
            // Collect all volunteer registrations for org's events
            List<Map<String, Object>> volunteersList = new ArrayList<>();
            java.util.Set<Long> seenUserIds = new java.util.HashSet<>();
            
            for (Event event : orgEvents) {
                List<EventRegistration> registrations = eventRegistrationRepository.findByEventId(event.getId());
                for (EventRegistration reg : registrations) {
                    if (!seenUserIds.contains(reg.getUserId())) {
                        seenUserIds.add(reg.getUserId());
                        Optional<User> userOpt = userRepository.findById(reg.getUserId());
                        if (userOpt.isPresent()) {
                            User user = userOpt.get();
                            Map<String, Object> volunteerData = new HashMap<>();
                            volunteerData.put("id", user.getId());
                            volunteerData.put("name", user.getFirstName() + " " + user.getLastName());
                            volunteerData.put("email", user.getEmail());
                            volunteerData.put("phone", user.getPhone());
                            volunteerData.put("city", user.getCity());
                            volunteerData.put("volunteerPoints", user.getVolunteerPoints());
                            volunteerData.put("eventsCompleted", user.getEventsCompleted());
                            volunteersList.add(volunteerData);
                        }
                    }
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("volunteers", volunteersList);
            response.put("totalVolunteers", volunteersList.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/events/{eventId}/volunteers")
    public ResponseEntity<?> getEventVolunteers(
            @PathVariable Long eventId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long orgId = extractOrgIdFromToken(authHeader);
            
            // Verify event belongs to this org
            Optional<Event> eventOpt = eventRepository.findById(eventId);
            if (!eventOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Event event = eventOpt.get();
            if (!event.getOrganizerId().equals(orgId)) {
                return ResponseEntity.status(403).body(Map.of("error", "Not authorized to view this event"));
            }
            
            // Get volunteers for this event
            List<EventRegistration> registrations = eventRegistrationRepository.findByEventId(eventId);
            List<Map<String, Object>> volunteersList = new ArrayList<>();
            
            for (EventRegistration reg : registrations) {
                Optional<User> userOpt = userRepository.findById(reg.getUserId());
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    Map<String, Object> volunteerData = new HashMap<>();
                    volunteerData.put("id", user.getId());
                    volunteerData.put("name", user.getFirstName() + " " + user.getLastName());
                    volunteerData.put("email", user.getEmail());
                    volunteerData.put("phone", user.getPhone());
                    volunteerData.put("registrationDate", reg.getRegistrationDate());
                    volunteerData.put("status", reg.getStatus());
                    volunteerData.put("attendanceConfirmed", reg.getAttendanceConfirmed());
                    volunteersList.add(volunteerData);
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("eventId", eventId);
            response.put("eventTitle", event.getTitle());
            response.put("volunteers", volunteersList);
            response.put("totalVolunteers", volunteersList.size());
            response.put("capacity", event.getCapacity());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // ===== Helper Methods =====
    
    private Long extractOrgIdFromToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtTokenProvider.getUserIdFromToken(token);
        }
        throw new RuntimeException("Invalid authorization header");
    }
}
