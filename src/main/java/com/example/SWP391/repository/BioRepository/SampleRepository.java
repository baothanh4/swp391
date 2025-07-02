package com.example.SWP391.repository.BioRepository;

import com.example.SWP391.entity.Sample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SampleRepository extends JpaRepository<Sample,Integer> {
    int countByCodeStartingWith(String prefix);
}
