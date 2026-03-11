package com.example.web_hr.controllers.api;

import com.example.web_hr.service.*;
import java.io.File;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class UploadController {

  private final MdbReaderService mdbReader;
  private final AttendanceDbService dbService;

  public UploadController(
    MdbReaderService mdbReader,
    AttendanceDbService dbService
  ) {
    this.mdbReader = mdbReader;
    this.dbService = dbService;
  }

  @PostMapping("/upload")
  public ResponseEntity<?> uploadMdb(@RequestParam("file") MultipartFile file)
    throws Exception {
    // 1. Simpan file sementara
    File temp = File.createTempFile("mdb-", ".mdb");
    file.transferTo(temp);

    // 2. Baca data MDB
    List<Map<String, Object>> data = mdbReader.readAttendance(
      temp.getAbsolutePath()
    );

    // 3. Simpan ke database
    dbService.saveAttendance(data, file.getOriginalFilename());

    return ResponseEntity.ok(
      Map.of("status", "success", "totalRecords", data.size())
    );
  }
}
