package com.example.SWP391.service.Customer;

import com.example.SWP391.repository.UserRepository.AdminRepository;
import com.example.SWP391.repository.UserRepository.ManagerRepository;
import com.example.SWP391.repository.UserRepository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IdGeneratorService {
    @Autowired
    private AdminRepository adminRepository;
    @Autowired private StaffRepository staffRepository;
    @Autowired private ManagerRepository managerRepository;

    private String formatId(String prefix, int number) {
        return String.format("%s%03d", prefix, number);
    }

    public String generateAdminId() {
        Integer max = adminRepository.findMaxAdminId();
        return formatId("ADM", (max != null ? max + 1 : 1));
    }

    public String generateStaffId() {
        Integer max = staffRepository.findMaxStaffId();
        return formatId("STF", (max != null ? max + 1 : 1));
    }

    public String generateManagerId() {
        Integer max = managerRepository.findMaxManagerId();
        return formatId("MAN", (max != null ? max + 1 : 1));
    }
}
