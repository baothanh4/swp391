package com.example.SWP391.repository.UserRepository;

import com.example.SWP391.entity.Booking.Booking;
import com.example.SWP391.entity.Otp.Account;
import com.example.SWP391.entity.User.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, String> {
    long count();
    @Query("SELECT c.fullName FROM Customer c WHERE c.customerID = :id")
    String findFullNameByCustomerID(@Param("id") String customerID);
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByPhone(String phone);
    Optional<Customer> findByAccount(Account account);
}
