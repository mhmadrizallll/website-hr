package com.example.web_hr.repository;

import com.example.web_hr.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {}
