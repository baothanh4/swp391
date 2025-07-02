package com.example.SWP391.service.Manager;

import com.example.SWP391.DTO.AuthUpdate.UpdateRequestDTO;
import com.example.SWP391.entity.User.Manager;
import com.example.SWP391.repository.UserRepository.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ManagerService {
    @Autowired
    ManagerRepository managerRepository;
    public Manager updateInfo(String managerID, UpdateRequestDTO request){
        Manager manager=managerRepository.findById(managerID).orElseThrow(()-> new RuntimeException("ManagerID not found"));
        if(request.getFullName()!=null){
            manager.setFullName(request.getFullName());
        }
        if(request.getDOB()!=null){
            manager.setDOB(request.getDOB());
        }
        if(request.getEmail()!=null){
            Optional<Manager> managerWithSameEmail=managerRepository.findByEmail(request.getEmail());
            if(managerWithSameEmail.isPresent() && !managerWithSameEmail.get().getManagerID().equals(managerID)){
                throw new RuntimeException("Email already in use");
            }
            manager.setEmail(request.getEmail());
        }
        if(request.getPhone()!=null){
            Optional<Manager> managerWithSamePhone=managerRepository.findByPhone(request.getPhone());
            if(managerWithSamePhone.isPresent() && !managerWithSamePhone.get().getManagerID().equals(managerID)){
                throw new RuntimeException("Phone already in use");
            }
            manager.setPhone(request.getPhone());
        }
        if(request.getAddress()!=null){
            manager.setAddress(request.getAddress());
        }
        if(request.getGender()!=null){
            manager.setGender(request.getGender());
        }
        return managerRepository.save(manager);
    }
}
