package com.example.web_hr.controllers.api;

import com.example.web_hr.service.AttendanceExportService;
import java.time.LocalDate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ExportController {

  private final AttendanceExportService attendanceExportService;

  public ExportController(AttendanceExportService attendanceExportService) {
    this.attendanceExportService = attendanceExportService;
  }

  @GetMapping("/export-txt")
  public ResponseEntity<String> exportTxt(
    @RequestParam String start,
    @RequestParam String end
  ) {
    LocalDate startDate = LocalDate.parse(start);
    LocalDate endDate = LocalDate.parse(end);

    String txt = attendanceExportService.exportTxt(startDate, endDate);

    // nama file dinamis
    String fileName = "attendance_" + start + "_" + end + ".txt";

    return ResponseEntity.ok()
      .header(
        HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=" + fileName
      )
      .contentType(MediaType.TEXT_PLAIN)
      .body(txt);
  }
}
