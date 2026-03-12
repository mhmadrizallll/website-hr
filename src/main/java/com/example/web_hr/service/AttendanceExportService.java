package com.example.web_hr.service;

import com.example.web_hr.entity.AttendanceLog;
import com.example.web_hr.repository.AttendanceLogRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AttendanceExportService {

  private final AttendanceLogRepository attendanceLogRepository;

  public AttendanceExportService(
    AttendanceLogRepository attendanceLogRepository
  ) {
    this.attendanceLogRepository = attendanceLogRepository;
  }

  public String exportTxt(LocalDate startDate, LocalDate endDate) {
    LocalDateTime start = startDate.atStartOfDay();
    LocalDateTime end = endDate.atTime(23, 59, 59);

    List<AttendanceLog> logs = attendanceLogRepository.findByCheckTimeBetween(
      start,
      end
    );

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    StringBuilder builder = new StringBuilder();

    for (AttendanceLog log : logs) {
      String badge = String.format(
        "%010d",
        Long.parseLong(log.getUser().getBadgeNumber())
      );

      String date = log.getCheckTime().format(dateFormatter);
      String time = log.getCheckTime().format(timeFormatter);

      builder
        .append(badge)
        .append(";")
        .append(date)
        .append(";")
        .append(time)
        .append("\n");
    }

    return builder.toString();
  }
}
