package com.example.SWP391.repository.OtpRepository;

import com.example.SWP391.entity.Otp.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {
    List<OtpVerification> findByEmail(String email);

    Optional<OtpVerification> findTopByEmailOrderByIdDesc(String email);
    Optional<OtpVerification> findTopByEmailOrderByExpirationTimeDesc(String email);

}
