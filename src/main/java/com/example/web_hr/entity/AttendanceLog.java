package com.example.web_hr.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_logs")
public class AttendanceLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long logId;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  private LocalDateTime checkTime;

  private String checkType;

  private Integer verifyCode;

  @ManyToOne
  @JoinColumn(name = "upload_id")
  private UploadHistory uploadHistory;

  // Getter & Setter
  public Long getLogId() {
    return logId;
  }

  public void setLogId(Long logId) {
    this.logId = logId;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public LocalDateTime getCheckTime() {
    return checkTime;
  }

  public void setCheckTime(LocalDateTime checkTime) {
    this.checkTime = checkTime;
  }

  public String getCheckType() {
    return checkType;
  }

  public void setCheckType(String checkType) {
    this.checkType = checkType;
  }

  public Integer getVerifyCode() {
    return verifyCode;
  }

  public void setVerifyCode(Integer verifyCode) {
    this.verifyCode = verifyCode;
  }

  public UploadHistory getUploadHistory() {
    return uploadHistory;
  }

  public void setUploadHistory(UploadHistory uploadHistory) {
    this.uploadHistory = uploadHistory;
  }
}
