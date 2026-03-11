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
import java.util.List;
import java.util.Map;
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
    // =====================
    // 0. Pastikan Department default ada (id = 1)
    // =====================
    Department department = departmentRepository
      .findById(1L)
      .orElseGet(() -> {
        Department d = new Department();
        d.setDeptId(1L);
        d.setDeptName("DefaultDept"); // sementara
        return departmentRepository.save(d);
      });

    // =====================
    // 0b. Update nama departemen jika ada nama baru dari MDB
    // =====================
    if (!data.isEmpty()) {
      Map<String, Object> firstRow = data.get(0);
      Object deptNameObj = firstRow.get("department");
      if (deptNameObj != null) {
        String deptName = deptNameObj.toString();
        department.setDeptName(deptName);
        departmentRepository.save(department);
      }
    }

    // =====================
    // 1. Buat UploadHistory
    // =====================
    UploadHistory uploadHistory = new UploadHistory();
    uploadHistory.setFileName(fileName);
    uploadHistory.setUploadTime(LocalDateTime.now());
    uploadHistory.setStatus("SUCCESS");
    uploadHistoryRepository.save(uploadHistory);

    // =====================
    // 2. Simpan Users dulu (jika belum ada)
    // =====================
    for (Map<String, Object> row : data) {
      Long userId = ((Number) row.get("userId")).longValue();
      String badgeNumber = (String) row.get("badgeNumber");
      String name = (String) row.get("name");

      userRepository
        .findById(userId)
        .orElseGet(() -> {
          User user = new User();
          user.setUserId(userId);
          user.setBadgeNumber(badgeNumber);
          user.setName(name);
          user.setIsActive(true);
          user.setDepartment(department); // pakai dept id 1
          return userRepository.save(user);
        });
    }

    // =====================
    // 3. Simpan AttendanceLogs
    // =====================
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
      "dd-MM-yyyy HH.mm"
    );

    for (Map<String, Object> row : data) {
      Long userId = ((Number) row.get("userId")).longValue();
      User user = userRepository.findById(userId).orElse(null);
      if (user == null) continue;

      AttendanceLog log = new AttendanceLog();
      log.setUser(user);
      log.setUploadHistory(uploadHistory);

      Object checkTimeObj = row.get("checkTime");
      if (checkTimeObj != null) {
        log.setCheckTime(
          LocalDateTime.parse(checkTimeObj.toString(), formatter)
        );
      }

      log.setCheckType((String) row.get("type"));
      attendanceLogRepository.save(log);
    }

    return uploadHistory;
  }
}
