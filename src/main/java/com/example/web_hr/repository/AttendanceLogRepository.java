package com.example.web_hr.repository;

import com.example.web_hr.entity.AttendanceLog;
import com.example.web_hr.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttendanceLogRepository
  extends JpaRepository<AttendanceLog, Long>
{
  boolean existsByUserAndCheckTime(User user, LocalDateTime checkTime);

  // ===============================
  // FIND LOGS BY USER IDS
  // ===============================
  @Query(
    """
        SELECT a FROM AttendanceLog a
        WHERE a.user.userId IN :userIds
    """
  )
  List<AttendanceLog> findByUserIds(@Param("userIds") Set<Long> userIds);

  // ===============================
  // EXPORT DATA
  // ===============================
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

  // ===============================
  // FIND LOGS BY USER (PAGINATION)
  // ===============================
  @Query(
    """
        SELECT a FROM AttendanceLog a
        JOIN FETCH a.user
        WHERE a.user.userId = :userId
        ORDER BY a.checkTime DESC
    """
  )
  Page<AttendanceLog> findByUserId(
    @Param("userId") Long userId,
    Pageable pageable
  );

  // ===============================
  // LAST ATTENDANCE PER USER
  // ===============================
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
  Page<AttendanceLog> findLastAttendance(Pageable pageable);

  // ===============================
  // SEARCH EMPLOYEE
  // ===============================
  @Query(
    """
        SELECT a FROM AttendanceLog a
        JOIN FETCH a.user u
        WHERE (
            LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(u.badgeNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        AND a.checkTime = (
            SELECT MAX(b.checkTime)
            FROM AttendanceLog b
            WHERE b.user = a.user
        )
        ORDER BY a.checkTime DESC
    """
  )
  Page<AttendanceLog> searchEmployees(
    @Param("keyword") String keyword,
    Pageable pageable
  );

  @Query(
    """
    SELECT a FROM AttendanceLog a
    JOIN FETCH a.user
    WHERE a.user.userId = :userId
    AND a.checkTime BETWEEN :start AND :end
    ORDER BY a.checkTime DESC
    """
  )
  Page<AttendanceLog> findByUserIdAndDate(
    @Param("userId") Long userId,
    @Param("start") LocalDateTime start,
    @Param("end") LocalDateTime end,
    Pageable pageable
  );
}
