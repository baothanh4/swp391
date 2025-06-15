package com.example.SWP391.repository.BookingRepository;

import com.example.SWP391.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, String> {
    List<Service> findByNameIn(List<String> names);
}
