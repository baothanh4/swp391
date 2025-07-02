package com.example.SWP391.repository.UserRepository;

import com.example.SWP391.entity.Otp.Account;
import com.example.SWP391.entity.User.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff,String> {
    Optional<Staff> findTopByOrderByStaffIDDesc();
    @Query(value = "SELECT MAX(CAST(SUBSTRING(staff_id, 4) AS UNSIGNED)) FROM staff", nativeQuery = true)
    Integer findMaxStaffId();
    Optional<Staff> findByAccount(Account account);
    Optional<Staff> findByEmail(String email);
    Optional<Staff> findByPhone(String phone);
}
