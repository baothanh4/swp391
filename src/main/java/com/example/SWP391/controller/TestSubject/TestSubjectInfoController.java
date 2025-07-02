package com.example.SWP391.controller.TestSubject;

import com.example.SWP391.DTO.AuthRequest.TestSubjectInfoRequestDTO;
import com.example.SWP391.entity.TestSubjectInfo;
import com.example.SWP391.service.TestSubject.TestSubjectInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test-subject-info")
@RequiredArgsConstructor
public class TestSubjectInfoController {
    private final TestSubjectInfoService testSubjectInfoService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody TestSubjectInfoRequestDTO request) {
        try {
            TestSubjectInfo created = testSubjectInfoService.createTestSubjectInfo(request);
            return ResponseEntity.ok(created);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
