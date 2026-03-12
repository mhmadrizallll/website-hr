package com.example.web_hr.repository;

import com.example.web_hr.entity.AttendanceLog;
import com.example.web_hr.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttendanceLogRepository
  extends JpaRepository<AttendanceLog, Long>
{
  boolean existsByUserAndCheckTime(User user, LocalDateTime checkTime);

  // FIXED: akses user.userId karena primary key bukan 'id'
  @Query("SELECT a FROM AttendanceLog a WHERE a.user.userId IN :userIds")
  List<AttendanceLog> findByUserIds(@Param("userIds") Set<Long> userIds);
}
