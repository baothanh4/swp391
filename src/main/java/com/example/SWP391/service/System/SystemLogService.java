package com.example.SWP391.service.System;

import com.example.SWP391.entity.SystemLog;
import com.example.SWP391.repository.SystemLogRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SystemLogService {
    @Autowired
    private SystemLogRepository logRepository;
    @Transactional
    public void log(String username,String action,String ip){
        try {
            SystemLog log = new SystemLog();
            log.setUsername(username);
            log.setAction(action);
            log.setTimestamp(LocalDateTime.now());
            log.setIpAddress(ip);
            logRepository.save(log);
        } catch (Exception e) {
            e.printStackTrace(); // Tạm thời để debug nếu có lỗi
        }
    }
}
