package com.example.SWP391.service.Customer;

import com.example.SWP391.entity.Otp.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    private final Account account;

    public CustomUserDetails(Account account){
        this.account = account;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return List.of(new SimpleGrantedAuthority("ROLE_" + account.getRole().toUpperCase()));
    }

    @Override
    public String getPassword(){
        return account.getPassword();
    }

    @Override
    public String getUsername(){
        return account.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() {
        return account.isEnabled(); // kiểm tra đúng trạng thái tài khoản
    }
}
