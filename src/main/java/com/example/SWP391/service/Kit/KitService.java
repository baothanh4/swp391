package com.example.SWP391.service.Kit;

import com.example.SWP391.entity.BioKit;
import com.example.SWP391.repository.BioRepository.BioKitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KitService {
    @Autowired
    private final BioKitRepository bioKitRepository;
    public void updateQuantity(String kitId,int quantity){
        BioKit bioKit=bioKitRepository.findById(kitId).orElseThrow(()->new RuntimeException("KitID not found"));
        bioKit.setQuantity(quantity);
        bioKitRepository.save(bioKit);
    }
}
