package com.example.SWP391.config;


import com.example.SWP391.security.JwtAuthenticationFilter;
import com.example.SWP391.service.Customer.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // nếu dùng JWT
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**","/api/auth/**","/api/admin/register"
                                ,"/api/admin/verify-otp","/api/admin/account/{id}",
                                "/error",
                                "/api/admin/account","/api/customers/**","/api/auth/update-password",
                                "/api/booking/**","/api/admin/dashboard/customers","/api/admin/kitInventory/all",
                                "/api/admin/kitInventory/available","/api/admin/{serviceId}/cost","/api/services/**",
                                "/api/staff/updateBooking/{id}","/api/test-subject-info/**","/api/manager/assign-staff/{assignedId}",
                                "/api/admin/system-log","/api/manager/kit/{KitID}","/api/staff/KitTransaction/{id}"
                        ).permitAll()

                        .requestMatchers("/api/admin/**").hasRole("Admin")
                        .requestMatchers("/api/customer/**").hasRole("Customer")
                        .requestMatchers("/api/manager/**").hasRole("Manager")
                        .requestMatchers("/api/staff/**").hasRole("Staff")
                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); // hoặc NoOpPasswordEncoder.getInstance() cho test
    }
}
