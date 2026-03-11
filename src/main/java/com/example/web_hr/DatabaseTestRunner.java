package com.example.web_hr;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.sql.DataSource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseTestRunner implements CommandLineRunner {

  private final DataSource dataSource;

  public DatabaseTestRunner(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public void run(String... args) throws Exception {
    System.out.println("⚡ Menjalankan DatabaseTestRunner...");

    try (Connection conn = dataSource.getConnection()) {
      System.out.println("✅ Koneksi ke Oracle berhasil!");

      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT sysdate FROM dual");
      if (rs.next()) {
        System.out.println("Current Oracle Date: " + rs.getString(1));
      }
    } catch (Exception e) {
      System.err.println("❌ Koneksi gagal: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
