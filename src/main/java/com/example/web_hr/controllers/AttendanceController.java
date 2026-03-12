package com.example.web_hr.controllers;

import com.example.web_hr.entity.AttendanceLog;
import com.example.web_hr.repository.AttendanceLogRepository;
import java.util.List; // <- ini yang kurang
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AttendanceController {

  private final AttendanceLogRepository attendanceLogRepository;

  public AttendanceController(AttendanceLogRepository attendanceLogRepository) {
    this.attendanceLogRepository = attendanceLogRepository;
  }

  @GetMapping("/employees/{id}/logs")
  public String employeeLogs(@PathVariable Long id, Model model) {
    List<AttendanceLog> logs = attendanceLogRepository.findByUserId(id);

    model.addAttribute("logs", logs);

    return "attendance_logs";
  }
}
