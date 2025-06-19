package com.example.SWP391.service.Customer;


import com.example.SWP391.DTO.AuthUpdate.CustomerUpdateRequestDTO;
import com.example.SWP391.entity.User.Customer;
import com.example.SWP391.repository.UserRepository.AccountRepository;
import com.example.SWP391.repository.UserRepository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {
    @Autowired
    private AccountRepository accRepo;

    @Autowired
    private CustomerRepository cusRepo;
    public Customer updateCustomer(String customerId, CustomerUpdateRequestDTO request){
        Customer customer=cusRepo.findById(customerId).orElseThrow(() -> new RuntimeException("Customer not found"));

        if(request.getFullName()!=null ){
            customer.setFullName(request.getFullName());
        }
        if(request.getDOB()!=null){
            customer.setDob(request.getDOB());
        }
        if(request.getEmail()!=null){
            Optional<Customer> customerWithSameEmail = cusRepo.findByEmail(request.getEmail());
            if (customerWithSameEmail.isPresent() && !customerWithSameEmail.get().getCustomerID().equals(customerId)) {
                throw new RuntimeException("Email is already in use by another customer.");
            }
            customer.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            Optional<Customer> customerWithSamePhone = cusRepo.findByPhone(request.getPhone());
            if (customerWithSamePhone.isPresent() && !customerWithSamePhone.get().getCustomerID().equals(customerId)) {
                throw new RuntimeException("Phone number is already in use by another customer.");
            }
            customer.setPhone(request.getPhone());
        }
        if(request.getAddress()!=null){
            customer.setAddress(request.getAddress());
        }
        if(request.getGender()!=null){
            customer.setGender(request.getGender());
        }

        return cusRepo.save(customer);
    }
}
