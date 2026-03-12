package com.example.web_hr.controllers;

import com.example.web_hr.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EmployeeController {

  private final UserRepository userRepository;

  public EmployeeController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @GetMapping("/employees")
  public String employees(Model model) {
    model.addAttribute("employees", userRepository.findAll());

    return "employees";
  }
}
