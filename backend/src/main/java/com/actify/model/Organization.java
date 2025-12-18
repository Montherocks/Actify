package com.actify.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "organizations")
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String orgType; // nonprofit, government, education, corporate, community
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String website;
    
    @Column(nullable = false)
    private String contactName;
    
    @Column(nullable = false)
    private String phone;
    
    @Column(columnDefinition = "TEXT")
    private String address;
    
    private String city;
    
    private String state;
    
    private String logoUrl;
    
    @Column(nullable = false)
    private Boolean verified = false;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    // Statistics
    @Column(nullable = false)
    private Integer totalEvents = 0;
    
    @Column(nullable = false)
    private Integer volunteersReached = 0;
    
    @Column(nullable = false)
    private Integer vpDistributed = 0;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getOrgType() { return orgType; }
    public void setOrgType(String orgType) { this.orgType = orgType; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    
    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    
    public Integer getTotalEvents() { return totalEvents; }
    public void setTotalEvents(Integer totalEvents) { this.totalEvents = totalEvents; }
    
    public Integer getVolunteersReached() { return volunteersReached; }
    public void setVolunteersReached(Integer volunteersReached) { this.volunteersReached = volunteersReached; }
    
    public Integer getVpDistributed() { return vpDistributed; }
    public void setVpDistributed(Integer vpDistributed) { this.vpDistributed = vpDistributed; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
