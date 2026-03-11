package com.example.web_hr.service;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Table;
import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class MdbReaderService {

  /**
   * Helper untuk ambil kolom dari row tanpa peduli case
   */
  private Object getIgnoreCase(Map<String, Object> row, String key) {
    for (String k : row.keySet()) {
      if (k.equalsIgnoreCase(key)) {
        return row.get(k);
      }
    }
    return null;
  }

  /**
   * Helper format checkTime ke "dd-MM-yyyy HH.mm"
   */
  private String formatCheckTime(Object checkTimeObj) {
    if (checkTimeObj == null) return null;

    LocalDateTime dateTime;

    if (checkTimeObj instanceof Date) {
      dateTime = ((Date) checkTimeObj).toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime();
    } else if (checkTimeObj instanceof LocalDateTime) {
      dateTime = (LocalDateTime) checkTimeObj;
    } else {
      dateTime = LocalDateTime.parse(checkTimeObj.toString());
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
      "dd-MM-yyyy HH.mm"
    );
    return dateTime.format(formatter);
  }

  /**
   * Baca file MDB dan kembalikan data CHECKINOUT lengkap dengan user + department
   */
  public List<Map<String, Object>> readAttendance(String filePath) {
    List<Map<String, Object>> result = new ArrayList<>();

    try {
      Database db = DatabaseBuilder.open(new File(filePath));

      // =====================
      // 1. READ DEPARTMENTS
      // =====================
      Map<Integer, String> deptMap = new HashMap<>();
      Table deptTable = db.getTable("DEPARTMENTS");

      if (deptTable != null) {
        for (Map<String, Object> row : deptTable) {
          Number deptIdNum = (Number) getIgnoreCase(row, "deptid");
          if (deptIdNum == null) continue;

          Integer deptId = deptIdNum.intValue();
          String deptName = String.valueOf(getIgnoreCase(row, "deptname"));

          deptMap.put(deptId, deptName);
        }
      }

      // =====================
      // 2. READ USERINFO
      // =====================
      Map<Integer, Map<String, Object>> userMap = new HashMap<>();
      Table userTable = db.getTable("USERINFO");

      if (userTable != null) {
        for (Map<String, Object> row : userTable) {
          Number userIdNum = (Number) getIgnoreCase(row, "userid");
          if (userIdNum == null) continue;

          Integer userId = userIdNum.intValue();
          userMap.put(userId, row);
        }
      }

      // =====================
      // 3. READ CHECKINOUT
      // =====================
      Table checkTable = db.getTable("CHECKINOUT");

      if (checkTable != null) {
        for (Map<String, Object> row : checkTable) {
          Number userIdNum = (Number) getIgnoreCase(row, "userid");
          if (userIdNum == null) continue;

          Integer userId = userIdNum.intValue();
          Map<String, Object> user = userMap.get(userId);
          if (user == null) continue;

          // Paksa pakai deptid = 2 (FIG5)
          Integer deptId = 2;
          String deptName = deptMap.get(deptId);

          Map<String, Object> data = new HashMap<>();
          data.put("userId", userId);
          data.put("name", getIgnoreCase(user, "name"));
          data.put("department", deptName);
          data.put(
            "checkTime",
            formatCheckTime(getIgnoreCase(row, "checktime"))
          );
          data.put("type", getIgnoreCase(row, "checktype"));

          result.add(data);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return result;
  }
}
