package com.example.web_hr.controllers;

import com.example.web_hr.entity.AttendanceLog;
import com.example.web_hr.repository.AttendanceLogRepository;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AttendanceController {

  private final AttendanceLogRepository attendanceLogRepository;

  public AttendanceController(AttendanceLogRepository attendanceLogRepository) {
    this.attendanceLogRepository = attendanceLogRepository;
  }

  // ===============================
  // EMPLOYEES PAGE
  // ===============================
  @GetMapping("/employees")
  public String employees(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(required = false) String keyword,
    Model model
  ) {
    Page<AttendanceLog> data;

    if (keyword != null && !keyword.isEmpty()) {
      data = attendanceLogRepository.searchEmployees(
        keyword,
        PageRequest.of(page, 10)
      );
    } else {
      data = attendanceLogRepository.findLastAttendance(
        PageRequest.of(page, 10)
      );
    }

    int start = Math.max(0, page - 2);
    int end = Math.min(data.getTotalPages() - 1, page + 2);

    model.addAttribute("logs", data.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", data.getTotalPages());
    model.addAttribute("keyword", keyword);
    model.addAttribute("start", start);
    model.addAttribute("end", end);

    return "employees";
  }

  // ===============================
  // EMPLOYEE LOGS (PAGINATION)
  // ===============================
  @GetMapping("/employees/{id}/logs")
  public String employeeLogs(
    @PathVariable Long id,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(required = false) String startDate,
    @RequestParam(required = false) String endDate,
    Model model
  ) {
    Page<AttendanceLog> data;

    if (
      startDate != null &&
      endDate != null &&
      !startDate.isEmpty() &&
      !endDate.isEmpty()
    ) {
      LocalDateTime start = LocalDateTime.parse(startDate + "T00:00:00");

      LocalDateTime end = LocalDateTime.parse(endDate + "T23:59:59");

      data = attendanceLogRepository.findByUserIdAndDate(
        id,
        start,
        end,
        PageRequest.of(page, 10)
      );
    } else {
      data = attendanceLogRepository.findByUserId(id, PageRequest.of(page, 10));
    }

    int start = Math.max(0, page - 2);
    int end = Math.min(data.getTotalPages() - 1, page + 2);

    model.addAttribute("logs", data.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", data.getTotalPages());
    model.addAttribute("userId", id);
    model.addAttribute("start", start);
    model.addAttribute("end", end);
    model.addAttribute("startDate", startDate);
    model.addAttribute("endDate", endDate);

    return "attendance_logs";
  }
}
