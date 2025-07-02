package com.example.SWP391.service.Kit;

import com.example.SWP391.entity.KitTransaction;
import com.example.SWP391.repository.BioRepository.KitTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KitTransactionService {
    @Autowired
    private final KitTransactionRepository kitTransactionRepository;
    public KitTransaction updateIsReceived(Long transactionID,boolean isReceived){
        KitTransaction kitTransaction=kitTransactionRepository.findById(transactionID).orElseThrow(()->new RuntimeException("TransactionID not found"));

        kitTransaction.setReceived(isReceived);

        kitTransactionRepository.save(kitTransaction);
        return kitTransaction;
    }
}
