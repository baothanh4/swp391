package com.example.SWP391.controller.Customer;

import com.example.SWP391.DTO.AuthUpdate.CustomerUpdateRequest;
import com.example.SWP391.service.Customer.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import com.example.SWP391.entity.Customer;
@RestController
@RequestMapping("/api/customers")
public class Customers {
    @Autowired
    private CustomerService customerService;

    @PatchMapping("/{id}")
    public ResponseEntity<Customer>  updateCustomer(@PathVariable("id") String customerId, @RequestBody CustomerUpdateRequest request){
        Customer updated= customerService.updateCustomer(customerId,request);
        return ResponseEntity.ok(updated);
    }
}
