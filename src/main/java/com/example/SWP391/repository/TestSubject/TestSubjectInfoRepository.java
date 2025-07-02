package com.example.SWP391.repository.TestSubject;

import com.example.SWP391.entity.TestSubjectInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestSubjectInfoRepository  extends JpaRepository<TestSubjectInfo,Long> {
}
