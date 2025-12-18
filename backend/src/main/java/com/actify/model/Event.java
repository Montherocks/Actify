package com.actify.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private String cause;
    
    @Column(nullable = false)
    private String location;
    
    private String city;
    
    private String state;
    
    private Double latitude;
    
    private Double longitude;
    
    @Column(nullable = false)
    private String date;
    
    private String time;
    
    private String endTime;
    
    private Integer duration;
    
    @Column(nullable = false)
    private Integer pointsReward = 100;
    
    @Column(nullable = false)
    private String status = "active";
    
    private Integer capacity = 50;
    
    private Integer volunteersRegistered = 0;
    
    @Column(columnDefinition = "TEXT")
    private String skillsNeeded;
    
    @Column(columnDefinition = "TEXT")
    private String requirements;
    
    private String contactEmail;
    
    private String contactPhone;
    
    private String imageUrl;
    
    private Long organizerId;
    
    private String organizerName;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCause() { return cause; }
    public void setCause(String cause) { this.cause = cause; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    
    public Integer getPointsReward() { return pointsReward; }
    public void setPointsReward(Integer points) { this.pointsReward = points; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    
    public Integer getVolunteersRegistered() { return volunteersRegistered; }
    public void setVolunteersRegistered(Integer volunteersRegistered) { this.volunteersRegistered = volunteersRegistered; }
    
    public String getSkillsNeeded() { return skillsNeeded; }
    public void setSkillsNeeded(String skillsNeeded) { this.skillsNeeded = skillsNeeded; }
    
    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
    
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public Long getOrganizerId() { return organizerId; }
    public void setOrganizerId(Long organizerId) { this.organizerId = organizerId; }
    
    public String getOrganizerName() { return organizerName; }
    public void setOrganizerName(String organizerName) { this.organizerName = organizerName; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

