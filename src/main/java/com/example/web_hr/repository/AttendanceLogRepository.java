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

  @Query("SELECT a FROM AttendanceLog a WHERE a.user.userId IN :userIds")
  List<AttendanceLog> findByUserIds(@Param("userIds") Set<Long> userIds);

  // export berdasarkan tanggal
  @Query(
    """
        SELECT a FROM AttendanceLog a
        JOIN FETCH a.user
        WHERE a.checkTime BETWEEN :start AND :end
        ORDER BY a.checkTime
    """
  )
  List<AttendanceLog> findByCheckTimeBetween(
    @Param("start") LocalDateTime start,
    @Param("end") LocalDateTime end
  );

  // ambil log berdasarkan user
  @Query(
    """
    SELECT a FROM AttendanceLog a
    JOIN FETCH a.user
    WHERE a.user.userId = :userId
    ORDER BY a.checkTime DESC
    """
  )
  List<AttendanceLog> findByUserId(@Param("userId") Long userId);

  @Query(
    """
    SELECT a FROM AttendanceLog a
    JOIN FETCH a.user u
    WHERE a.checkTime = (
        SELECT MAX(b.checkTime)
        FROM AttendanceLog b
        WHERE b.user = a.user
    )
    ORDER BY a.checkTime DESC
    """
  )
  List<AttendanceLog> findLastAttendance();
}
