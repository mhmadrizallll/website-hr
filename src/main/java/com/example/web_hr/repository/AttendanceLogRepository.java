package com.example.web_hr.repository;

import com.example.web_hr.entity.AttendanceLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceLogRepository
  extends JpaRepository<AttendanceLog, Long> {}
