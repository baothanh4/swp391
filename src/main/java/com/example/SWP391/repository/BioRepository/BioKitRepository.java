package com.example.SWP391.repository.BioRepository;

import com.example.SWP391.entity.BioKit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BioKitRepository extends JpaRepository<BioKit,String> {
    List<BioKit> findByIsAvailable(boolean isAvailable);
    Optional<BioKit> findFirstByIsAvailableTrueAndQuantityGreaterThan(int quantity);
}
