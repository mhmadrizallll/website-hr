package com.example.web_hr.service;

import com.example.web_hr.entity.*;
import com.example.web_hr.repository.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
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
    // 1. Department
    // =====================
    Department department = departmentRepository
      .findById(1L)
      .orElseGet(() -> {
        Department d = new Department();
        d.setDeptName("DefaultDept");
        return departmentRepository.save(d);
      });

    Object deptNameObj = data.get(0).get("department");
    if (deptNameObj != null) {
      department.setDeptName(deptNameObj.toString());
    }

    // =====================
    // 2. UploadHistory
    // =====================
    UploadHistory uploadHistory = new UploadHistory();
    uploadHistory.setFileName(fileName);
    uploadHistory.setUploadTime(LocalDateTime.now());
    uploadHistory.setStatus("SUCCESS");
    uploadHistoryRepository.save(uploadHistory);

    // =====================
    // 3. Collect userIds
    // =====================
    Set<Long> userIds = data
      .stream()
      .map(r -> ((Number) r.get("userId")).longValue())
      .collect(Collectors.toSet());

    // =====================
    // 4. Load existing users
    // =====================
    List<User> existingUsers = userRepository.findAllById(userIds);

    Map<Long, User> userMap = existingUsers
      .stream()
      .collect(Collectors.toMap(User::getUserId, u -> u));

    List<User> newUsers = new ArrayList<>();

    for (Map<String, Object> row : data) {
      Long userId = ((Number) row.get("userId")).longValue();

      if (!userMap.containsKey(userId)) {
        User user = new User();
        user.setUserId(userId);
        user.setBadgeNumber((String) row.get("badgeNumber"));
        user.setName((String) row.get("name"));
        user.setIsActive(true);
        user.setDepartment(department);

        newUsers.add(user);
        userMap.put(userId, user);
      }
    }

    if (!newUsers.isEmpty()) {
      userRepository.saveAll(newUsers);
    }

    // =====================
    // 5. Existing logs
    // =====================
    List<AttendanceLog> existingLogs = attendanceLogRepository.findByUserIds(
      userIds
    );

    Set<String> existingKeys = existingLogs
      .stream()
      .map(l -> l.getUser().getUserId() + "_" + l.getCheckTime())
      .collect(Collectors.toSet());

    // =====================
    // 6. Create new logs
    // =====================
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
      "dd-MM-yyyy HH.mm"
    );

    List<AttendanceLog> newLogs = new ArrayList<>();

    for (Map<String, Object> row : data) {
      Long userId = ((Number) row.get("userId")).longValue();
      User user = userMap.get(userId);

      Object checkTimeObj = row.get("checkTime");
      if (checkTimeObj == null) continue;

      LocalDateTime checkTime = LocalDateTime.parse(
        checkTimeObj.toString(),
        formatter
      );

      String key = userId + "_" + checkTime;

      if (existingKeys.contains(key)) continue;

      AttendanceLog log = new AttendanceLog();
      log.setUser(user);
      log.setUploadHistory(uploadHistory);
      log.setCheckTime(checkTime);
      log.setCheckType((String) row.get("type"));

      newLogs.add(log);
    }

    // =====================
    // 7. Batch insert
    // =====================
    if (!newLogs.isEmpty()) {
      attendanceLogRepository.saveAll(newLogs);
    }

    return uploadHistory;
  }
}
