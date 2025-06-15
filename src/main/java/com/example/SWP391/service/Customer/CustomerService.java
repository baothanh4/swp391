package com.example.SWP391.service.Customer;

import com.example.SWP391.entity.Customer;
import com.example.SWP391.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateService {
    @Autowired
    private AccountRepository accRepo;

    public void updateCustomer(Customer customer){

    }
}
