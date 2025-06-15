package com.example.SWP391.service.Admin;

import com.example.SWP391.DTO.AuthRequest.RegisterRequest;
import com.example.SWP391.DTO.AuthUpdate.AccountUpdate;
import com.example.SWP391.entity.*;
import com.example.SWP391.entity.Otp.Account;
import com.example.SWP391.entity.User.Admin;
import com.example.SWP391.entity.User.Manager;
import com.example.SWP391.entity.User.Staff;
import com.example.SWP391.repository.BioRepository.BioKitRepository;
import com.example.SWP391.repository.UserRepository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    @Autowired private AdminRepository adminRepo;
    @Autowired private StaffRepository staffRepo;
    @Autowired private ManagerRepository managerRepo;
    @Autowired private AccountRepository accountRepo;
    @Autowired private CustomerRepository customerRepository;
    @Autowired
    private BioKitRepository bioKitRepository;
    public String generateAdminID() {
        String prefix = "ADM";
        String lastId = adminRepo.findTopByOrderByAdminIDDesc()
                .map(Admin::getAdminID).orElse(null);

        int number = (lastId != null && lastId.startsWith(prefix)) ?
                Integer.parseInt(lastId.substring(prefix.length())) : 0;
        number++;

        return String.format("%s%03d", prefix, number);
    }

    public String generateStaffID() {
        String prefix = "STF";
        String lastId = staffRepo.findTopByOrderByStaffIDDesc()
                .map(Staff::getStaffID).orElse(null);

        int number = (lastId != null && lastId.startsWith(prefix)) ?
                Integer.parseInt(lastId.substring(prefix.length())) : 0;
        number++;

        return String.format("%s%03d", prefix, number);
    }

    public String generateManagerID() {
        String prefix = "MAN";
        String lastId = managerRepo.findTopByOrderByManagerIDDesc()
                .map(Manager::getManagerID).orElse(null);

        int number = (lastId != null && lastId.startsWith(prefix)) ?
                Integer.parseInt(lastId.substring(prefix.length())) : 0;
        number++;

        return String.format("%s%03d", prefix, number);
    }

    public void registerAccount(RegisterRequest request) {
        Account account = new Account();
        account.setUsername(request.getUsername());
        account.setPassword(request.getPassword()); // TODO: hash password
        account.setEmail(request.getEmail());
        account.setPhone(request.getPhone());
        account.setRole(request.getRole());
        account.setCreateAt(LocalDate.now());
        account.setEnabled(false);

        account = accountRepo.save(account);
        System.out.println("Received full name: " + request.getFullName());
        switch (request.getRole()) {
            case "ROLE_ADMIN" -> {
                Admin admin = new Admin();
                admin.setAdminID(generateAdminID());
                admin.setFullName(request.getFullName());
                admin.setAccount(account);
                account.setAdmin(admin);
                accountRepo.save(account);
            }
            case "ROLE_STAFF" -> {
                Staff staff = new Staff();
                staff.setStaffID(generateStaffID());
                staff.setFullName(request.getFullName());
                staff.setAccount(account);
                account.setStaff(staff);
                staffRepo.save(staff);
            }
            case "ROLE_MANAGER" -> {
                Manager manager = new Manager();
                manager.setManagerID(generateManagerID());
                manager.setFullName(request.getFullName());
                manager.setAccount(account);
                account.setManager(manager);
                managerRepo.save(manager);
            }
        }
    }
    public void updateAccount(int accountID,AccountUpdate update){
        Optional<Account> optionalAccount = accountRepo.findById(accountID);
        if (optionalAccount.isEmpty()) {
            throw new RuntimeException("Account not found with id: " + accountID);
        }

        Account account = optionalAccount.get();

        // Nếu muốn bắt buộc các trường thì validate ở đây
        if (update.getPassword() != null && !update.getPassword().isBlank()) {
            account.setPassword(update.getPassword());
        }

        if (update.getEmail() != null && !update.getEmail().isBlank()) {
            Account accountWithSameEmail=accountRepo.findByEmail(update.getEmail());
            if(accountWithSameEmail!=null && accountWithSameEmail.equals(update.getEmail())){
                throw new RuntimeException("Email is already in use by another account");
            }
            account.setEmail(update.getEmail());
        }


        if (update.getRole() != null) {
            account.setRole(update.getRole());
        }
        if (update.getPhone() != null && !update.getPhone().isBlank()) {
            Optional<Account> accountWithSamePhone = accountRepo.findByPhone(update.getPhone());
            if (accountWithSamePhone.isPresent() && accountWithSamePhone.get().getAccountID() != accountID) {
                throw new RuntimeException("Phone number is already in use by another account.");
            }
            account.setPhone(update.getPhone());
        }

        account.setEnabled(update.isEnabled()); // gán luôn true/false

        accountRepo.save(account);
    }

    public List<BioKit> getAllKits() {
        return bioKitRepository.findAll();
    }

    public List<BioKit> getAvailableKits() {
        return bioKitRepository.findByIsAvailable(true);
    }
}
