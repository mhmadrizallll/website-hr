package com.example.web_hr.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "upload_history")
public class UploadHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long uploadId;

  private String fileName;

  private LocalDateTime uploadTime;

  private String status;

  @OneToMany(mappedBy = "uploadHistory")
  private List<AttendanceLog> logs;

  // Getter & Setter
  public Long getUploadId() {
    return uploadId;
  }

  public void setUploadId(Long uploadId) {
    this.uploadId = uploadId;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public LocalDateTime getUploadTime() {
    return uploadTime;
  }

  public void setUploadTime(LocalDateTime uploadTime) {
    this.uploadTime = uploadTime;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public List<AttendanceLog> getLogs() {
    return logs;
  }

  public void setLogs(List<AttendanceLog> logs) {
    this.logs = logs;
  }
}
