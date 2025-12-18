package com.actify.repository;

import com.actify.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Optional<Organization> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Organization> findByVerified(boolean verified);
    List<Organization> findByActive(boolean active);
}
