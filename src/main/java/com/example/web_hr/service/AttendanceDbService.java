package com.example.web_hr.service;

import com.example.web_hr.entity.AttendanceLog;
import com.example.web_hr.entity.Department;
import com.example.web_hr.entity.UploadHistory;
import com.example.web_hr.entity.User;
import com.example.web_hr.repository.AttendanceLogRepository;
import com.example.web_hr.repository.DepartmentRepository;
import com.example.web_hr.repository.UploadHistoryRepository;
import com.example.web_hr.repository.UserRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AttendanceDbService {

  private final UserRepository userRepository;
  private final DepartmentRepository departmentRepository;
  private final AttendanceLogRepository attendanceLogRepository;
  private final UploadHistoryRepository uploadHistoryRepository;

  public AttendanceDbService(
    UserRepository userRepository,
    DepartmentRepository departmentRepository,
    AttendanceLogRepository attendanceLogRepository,
    UploadHistoryRepository uploadHistoryRepository
  ) {
    this.userRepository = userRepository;
    this.departmentRepository = departmentRepository;
    this.attendanceLogRepository = attendanceLogRepository;
    this.uploadHistoryRepository = uploadHistoryRepository;
  }

  @Transactional
  public UploadHistory saveAttendance(
    List<Map<String, Object>> data,
    String fileName
  ) {
    if (data.isEmpty()) {
      throw new IllegalArgumentException("Data MDB kosong");
    }

    // =====================
    // 0. Pastikan Department default ada
    // =====================
    Department department = departmentRepository
      .findById(1L)
      .orElseGet(() -> {
        Department d = new Department();
        d.setDeptName("DefaultDept");
        return departmentRepository.save(d);
      });

    // Update nama department jika ada dari data MDB
    Object deptNameObj = data.get(0).get("department");
    if (deptNameObj != null) {
      department.setDeptName(deptNameObj.toString());
    }

    // =====================
    // 1. Simpan UploadHistory
    // =====================
    UploadHistory uploadHistory = new UploadHistory();
    uploadHistory.setFileName(fileName);
    uploadHistory.setUploadTime(LocalDateTime.now());
    uploadHistory.setStatus("SUCCESS");
    uploadHistoryRepository.save(uploadHistory);

    // =====================
    // 2. Simpan Users (baru atau update department)
    // =====================
    Set<Long> userIds = new HashSet<>();
    for (Map<String, Object> row : data) {
      Long userId = ((Number) row.get("userId")).longValue();
      userIds.add(userId);

      String badgeNumber = (String) row.get("badgeNumber");
      String name = (String) row.get("name");

      User user = userRepository
        .findById(userId)
        .orElseGet(() -> {
          User u = new User();
          u.setUserId(userId);
          u.setBadgeNumber(badgeNumber);
          u.setName(name);
          u.setIsActive(true);
          return u;
        });

      user.setDepartment(department);
      userRepository.save(user);
    }

    // =====================
    // 3. Ambil semua AttendanceLog existing untuk user di data
    // =====================
    List<AttendanceLog> existingLogs = attendanceLogRepository.findByUserIds(
      userIds
    );

    // Buat set cepat cek duplikat: "userId_checkTime"
    Set<String> existingKeys = existingLogs
      .stream()
      .map(
        log -> log.getUser().getUserId() + "_" + log.getCheckTime().toString()
      )
      .collect(Collectors.toSet());

    // =====================
    // 4. Prepare list log baru
    // =====================
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
      "dd-MM-yyyy HH.mm"
    );
    List<AttendanceLog> newLogs = new ArrayList<>();

    for (Map<String, Object> row : data) {
      Long userId = ((Number) row.get("userId")).longValue();
      User user = userRepository.findById(userId).orElse(null);
      if (user == null) continue;

      Object checkTimeObj = row.get("checkTime");
      if (checkTimeObj == null) continue;

      LocalDateTime checkTime = LocalDateTime.parse(
        checkTimeObj.toString(),
        formatter
      );
      String key = userId + "_" + checkTime.toString();

      if (existingKeys.contains(key)) continue; // skip jika sudah ada

      AttendanceLog log = new AttendanceLog();
      log.setUser(user);
      log.setUploadHistory(uploadHistory);
      log.setCheckTime(checkTime);
      log.setCheckType((String) row.get("type"));

      newLogs.add(log);
    }

    // =====================
    // 5. Batch insert log baru
    // =====================
    if (!newLogs.isEmpty()) {
      attendanceLogRepository.saveAll(newLogs);
    }

    return uploadHistory;
  }
}
