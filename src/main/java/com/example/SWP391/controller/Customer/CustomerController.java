package com.example.SWP391.controller.Customer;


import com.example.SWP391.DTO.AuthUpdate.CustomerUpdateRequestDTO;
import com.example.SWP391.repository.UserRepository.CustomerRepository;
import com.example.SWP391.service.Customer.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import com.example.SWP391.entity.User.Customer;

import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;
    @PatchMapping("/{id}")
    public ResponseEntity<?>  updateCustomer(@PathVariable("id") String customerId, @RequestBody CustomerUpdateRequestDTO request){
        try {
            Customer updated= customerService.updateCustomer(customerId,request);
            return ResponseEntity.ok(updated);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
