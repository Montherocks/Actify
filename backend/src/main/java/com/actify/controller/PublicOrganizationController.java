package com.actify.controller;

import com.actify.model.Organization;
import com.actify.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/organizations")
@CrossOrigin(origins = "*")
public class PublicOrganizationController {
    
    @Autowired
    private OrganizationRepository organizationRepository;
    
    // Get all organizations (for admin panel)
    @GetMapping
    public ResponseEntity<?> getAllOrganizations() {
        List<Organization> orgs = organizationRepository.findAll();
        
        // Remove passwords from response
        orgs.forEach(org -> org.setPassword(null));
        
        return ResponseEntity.ok(orgs);
    }
    
    // Get organization by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrganization(@PathVariable Long id) {
        Optional<Organization> orgOpt = organizationRepository.findById(id);
        
        if (orgOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Organization org = orgOpt.get();
        org.setPassword(null); // Don't expose password
        
        return ResponseEntity.ok(org);
    }
    
    // Verify organization (admin action)
    @PutMapping("/{id}/verify")
    public ResponseEntity<?> verifyOrganization(@PathVariable Long id) {
        Optional<Organization> orgOpt = organizationRepository.findById(id);
        
        if (orgOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Organization org = orgOpt.get();
        org.setVerified(true);
        organizationRepository.save(org);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Organization verified successfully");
        response.put("organization", org);
        
        return ResponseEntity.ok(response);
    }
    
    // Unverify organization (admin action)
    @PutMapping("/{id}/unverify")
    public ResponseEntity<?> unverifyOrganization(@PathVariable Long id) {
        Optional<Organization> orgOpt = organizationRepository.findById(id);
        
        if (orgOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Organization org = orgOpt.get();
        org.setVerified(false);
        organizationRepository.save(org);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Organization verification revoked");
        response.put("organization", org);
        
        return ResponseEntity.ok(response);
    }
    
    // Get verified organizations only
    @GetMapping("/verified")
    public ResponseEntity<?> getVerifiedOrganizations() {
        List<Organization> orgs = organizationRepository.findByVerified(true);
        
        // Remove passwords from response
        orgs.forEach(org -> org.setPassword(null));
        
        return ResponseEntity.ok(orgs);
    }
}
