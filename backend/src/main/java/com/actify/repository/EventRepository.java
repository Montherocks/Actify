package com.actify.repository;

import com.actify.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByOrganizerId(Long organizerId);
    List<Event> findByStatus(String status);
    List<Event> findByCause(String cause);
    List<Event> findByCity(String city);
}

