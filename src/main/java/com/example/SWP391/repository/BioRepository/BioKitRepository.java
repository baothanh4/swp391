package com.example.SWP391.repository;

import com.example.SWP391.entity.BioKit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface BioKitRepository extends JpaRepository<BioKit,Long> {
    List<BioKit> findByIsAvailable(int isAvailable);
}
