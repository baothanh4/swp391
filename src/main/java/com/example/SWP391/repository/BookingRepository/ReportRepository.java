package com.example.SWP391.repository.BookingRepository;

import com.example.SWP391.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report,Integer> {
    List<Report> findByStaff_StaffID(String staffId);
    List<Report> findByManager_ManagerID(String managerId);
}
