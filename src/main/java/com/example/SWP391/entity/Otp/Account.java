package com.example.SWP391.entity.Otp;

import com.example.SWP391.entity.User.Admin;
import com.example.SWP391.entity.User.Customer;
import com.example.SWP391.entity.User.Manager;
import com.example.SWP391.entity.User.Staff;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "Account")
@Getter
@Setter
@JsonIgnoreProperties({"customer", "admin", "staff", "manager"})
public class Account implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int accountID;
    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;
    private String phone;
    private String role;
    @Column(name = "create_at")
    private LocalDate createAt;

    @Column(nullable = false)
    private boolean enabled = false;
    @Column(name = "Fullname",nullable = false)
    private String fullname;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Customer customer;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Admin admin;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Staff staff;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Manager manager;

    // === IMPLEMENTATION OF UserDetails ===

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.toUpperCase()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Có thể thay đổi nếu muốn account có hạn
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Có thể thêm field `locked` để điều khiển
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Có thể thêm logic hết hạn mật khẩu
    }

    @Override
    public boolean isEnabled() {
        return this.enabled; // QUAN TRỌNG: đây là field bạn muốn kiểm tra
    }
}
