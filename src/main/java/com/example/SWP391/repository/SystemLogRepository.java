package com.example.SWP391.repository;

import com.example.SWP391.entity.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface SystemRepository extends JpaRepository<SystemLog,Long> {
    List<SystemLog> findByAction(String action);
    List<SystemLog> findByUsername(String username);
}
