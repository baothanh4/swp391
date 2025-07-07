package com.example.SWP391.service.Customer;

import com.example.SWP391.DTO.AuthUpdate.UpdateRequestDTO;
import com.example.SWP391.DTO.EntityDTO.CustomerDTO;
import com.example.SWP391.DTO.EntityDTO.FeedbackDTO;
import com.example.SWP391.entity.Booking.Booking;
import com.example.SWP391.entity.Feedback;
import com.example.SWP391.entity.User.Customer;
import com.example.SWP391.repository.BookingRepository.BookingRepository;
import com.example.SWP391.repository.BookingRepository.FeedbackRepository;
import com.example.SWP391.repository.UserRepository.AccountRepository;
import com.example.SWP391.repository.UserRepository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Thêm import này

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    @Autowired private AccountRepository accRepo;
    @Autowired private CustomerRepository cusRepo;
    @Autowired BookingRepository bookingRepository;
    @Autowired FeedbackRepository feedbackRepository;

    public Customer updateCustomer(String customerId, UpdateRequestDTO request){
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

    public void createFeedback(int bookingID,String customerID,FeedbackDTO dto){
        Booking booking=bookingRepository.findById(bookingID).orElseThrow(()->new IllegalArgumentException("Booking not found"));

        if(booking.getFeedback()!=null){
            throw new IllegalStateException("Feedback for this booking already exists");
        }

        Customer customer=cusRepo.findById(customerID).orElseThrow(()->new IllegalArgumentException("Customer not found"));

        if(!booking.getCustomer().getCustomerID().equals(customerID)){
            throw new IllegalStateException("this booking does not belong to customer");
        }

        if(!"COMPLETED".equalsIgnoreCase(booking.getStatus())){
            throw new IllegalStateException("Only completed booking can be reviewed");
        }

        Feedback feedback=new Feedback();
        feedback.setTitle(dto.getTitle());
        feedback.setContent(dto.getContent());
        feedback.setRating(dto.getRating());
        feedback.setCreateAt(LocalDate.now());
        feedback.setCustomer(customer);
        feedback.setBooking(booking);

        booking.setFeedback(feedback);
        feedbackRepository.save(feedback);

    }

    /**
     * Mapping Customer sang CustomerDTO KHÔNG có feedbackList
     */
    public CustomerDTO converToDTO(Customer customer){
        CustomerDTO customerDTO=new CustomerDTO();
        customerDTO.setFullName(customer.getFullName());
        customerDTO.setDob(customer.getDob());
        customerDTO.setEmail(customer.getEmail());
        customerDTO.setPhone(customer.getPhone());
        customerDTO.setAddress(customer.getAddress());
        customerDTO.setGender(customer.getGender());
        return customerDTO;
    }

    /**
     * Mapping Customer sang CustomerDTO CÓ feedbackList
     * Hàm này cần được gọi khi session Hibernate còn mở (bên trong @Transactional hoặc fetch join)
     */
    @Transactional
    public CustomerDTO convertToDTOWithFeedback(String customerId) {
        Customer customer = cusRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setFullName(customer.getFullName());
        customerDTO.setDob(customer.getDob());
        customerDTO.setEmail(customer.getEmail());
        customerDTO.setPhone(customer.getPhone());
        customerDTO.setAddress(customer.getAddress());
        customerDTO.setGender(customer.getGender());

        // Mapping feedbackList
        if (customer.getFeedbackList() != null) {
            List<FeedbackDTO> feedbackDTOList = customer.getFeedbackList().stream().map(feedback -> {
                FeedbackDTO dto = new FeedbackDTO();
                dto.setTitle(feedback.getTitle());
                dto.setContent(feedback.getContent());
                dto.setRating(feedback.getRating());
                dto.setCreateAt(feedback.getCreateAt());
                // Có thể set thêm các trường khác nếu FeedbackDTO có
                return dto;
            }).collect(Collectors.toList());
            customerDTO.setFeedbackList(feedbackDTOList);
        }

        return customerDTO;
    }
}