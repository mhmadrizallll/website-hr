package com.example.web_hr.repository;

import com.example.web_hr.entity.UploadHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadHistoryRepository
  extends JpaRepository<UploadHistory, Long> {}
