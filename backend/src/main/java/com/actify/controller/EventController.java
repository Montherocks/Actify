package com.actify.controller;

import com.actify.model.Event;
import com.actify.model.EventRegistration;
import com.actify.model.User;
import com.actify.repository.EventRepository;
import com.actify.repository.EventRegistrationRepository;
import com.actify.repository.UserRepository;
import com.actify.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/events")
public class EventController {
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private EventRegistrationRepository eventRegistrationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @GetMapping
    public ResponseEntity<?> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return ResponseEntity.ok(events);
    }
    
    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody Event event) {
        Event savedEvent = eventRepository.save(event);
        return ResponseEntity.ok(savedEvent);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getEvent(@PathVariable Long id) {
        Optional<Event> event = eventRepository.findById(id);
        if(event.isPresent()) {
            return ResponseEntity.ok(event.get());
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/{id}/register")
    public ResponseEntity<?> registerForEvent(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Extract user ID from JWT token
            String tokenValue = token.replace("Bearer ", "");
            Long userId = jwtTokenProvider.getUserIdFromToken(tokenValue);
            
            // Check if event exists
            Optional<Event> eventOpt = eventRepository.findById(id);
            if (!eventOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Event not found");
                return ResponseEntity.badRequest().body(response);
            }
            
            Event event = eventOpt.get();
            
            // Check if event is approved/active
            if (!"active".equals(event.getStatus())) {
                response.put("success", false);
                response.put("message", "This event is not yet approved for registration");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Check if already registered
            if (eventRegistrationRepository.existsByUserIdAndEventId(userId, id)) {
                response.put("success", false);
                response.put("message", "You are already registered for this event");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Check capacity
            if (event.getCapacity() != null && event.getVolunteersRegistered() >= event.getCapacity()) {
                response.put("success", false);
                response.put("message", "Event is at full capacity");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Create registration
            EventRegistration registration = new EventRegistration();
            registration.setUserId(userId);
            registration.setEventId(id);
            registration.setStatus("registered");
            eventRegistrationRepository.save(registration);
            
            // Update volunteer count
            event.setVolunteersRegistered(event.getVolunteersRegistered() + 1);
            eventRepository.save(event);
            
            response.put("success", true);
            response.put("message", "Successfully registered for event!");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error registering for event: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @DeleteMapping("/{id}/register")
    public ResponseEntity<?> unregisterFromEvent(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String tokenValue = token.replace("Bearer ", "");
            Long userId = jwtTokenProvider.getUserIdFromToken(tokenValue);
            
            Optional<EventRegistration> regOpt = eventRegistrationRepository.findByUserIdAndEventId(userId, id);
            if (!regOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "You are not registered for this event");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Delete registration
            eventRegistrationRepository.delete(regOpt.get());
            
            // Update volunteer count
            Optional<Event> eventOpt = eventRepository.findById(id);
            if (eventOpt.isPresent()) {
                Event event = eventOpt.get();
                event.setVolunteersRegistered(Math.max(0, event.getVolunteersRegistered() - 1));
                eventRepository.save(event);
            }
            
            response.put("success", true);
            response.put("message", "Successfully unregistered from event");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error unregistering from event");
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @GetMapping("/{id}/registrations")
    public ResponseEntity<?> getEventRegistrations(@PathVariable Long id) {
        List<EventRegistration> registrations = eventRegistrationRepository.findByEventId(id);
        
        // Enrich with user data
        for (EventRegistration reg : registrations) {
            Optional<User> userOpt = userRepository.findById(reg.getUserId());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                reg.setUserName(user.getFirstName() + " " + user.getLastName());
                reg.setUserEmail(user.getEmail());
            }
        }
        
        return ResponseEntity.ok(registrations);
    }
    
    @GetMapping("/user/registered")
    public ResponseEntity<?> getUserRegisteredEvents(@RequestHeader("Authorization") String token) {
        try {
            String tokenValue = token.replace("Bearer ", "");
            Long userId = jwtTokenProvider.getUserIdFromToken(tokenValue);
            
            List<EventRegistration> registrations = eventRegistrationRepository.findByUserId(userId);
            return ResponseEntity.ok(registrations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
        }
    }
}
