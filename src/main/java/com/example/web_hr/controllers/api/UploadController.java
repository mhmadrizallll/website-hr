package com.example.web_hr.controllers.api;

import com.example.web_hr.service.MdbReaderService;
import java.io.File;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

  @Autowired
  private MdbReaderService mdbReaderService;

  @PostMapping("/mdb")
  public List<Map<String, Object>> uploadMdb(
    @RequestParam("file") MultipartFile file
  ) {
    try {
      File tempFile = File.createTempFile("absensi", ".mdb");
      file.transferTo(tempFile);

      return mdbReaderService.readAttendance(tempFile.getAbsolutePath());
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
