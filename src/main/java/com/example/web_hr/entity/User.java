package com.example.web_hr.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

  @Id
  private Long userId; // ambil dari USERINFO.USERID

  private String badgeNumber;

  private String name;

  private Boolean isActive;

  @ManyToOne
  @JoinColumn(name = "dept_id")
  private Department department;

  @OneToMany(mappedBy = "user")
  private List<AttendanceLog> attendanceLogs;

  // Getter & Setter
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getBadgeNumber() {
    return badgeNumber;
  }

  public void setBadgeNumber(String badgeNumber) {
    this.badgeNumber = badgeNumber;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Boolean getIsActive() {
    return isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }

  public Department getDepartment() {
    return department;
  }

  public void setDepartment(Department department) {
    this.department = department;
  }

  public List<AttendanceLog> getAttendanceLogs() {
    return attendanceLogs;
  }

  public void setAttendanceLogs(List<AttendanceLog> attendanceLogs) {
    this.attendanceLogs = attendanceLogs;
  }
}
