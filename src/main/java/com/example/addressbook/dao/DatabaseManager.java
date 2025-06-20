// src/main/java/com/example/addressbook/dao/DatabaseManager.java
package com.example.addressbook.dao;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    // Имя файла БД
    private static final String DB_FILE_NAME = "addressbook.db";
    // URL для JDBC
    private static String DB_URL;

    static {
        try {
            // Определяем абсолютный путь к файлу БД.
            // При запуске из IDE или `mvn javafx:run` это будет корень проекта.
            // При запуске из JAR это будет директория, где лежит JAR.
            String dbFilePath = Paths.get(DB_FILE_NAME).toAbsolutePath().toString();
            DB_URL = "jdbc:sqlite:" + dbFilePath;
            System.out.println("Database URL set to: " + DB_URL);

            // Убедимся, что драйвер загружен (для некоторых старых систем или конфигураций)
            Class.forName("org.sqlite.JDBC");

        } catch (Exception e) {
            System.err.println("Could not determine database path or load driver: " + e.getMessage());
            e.printStackTrace();
            // Аварийный вариант, если что-то пошло не так - попытаться создать в текущей директории
            DB_URL = "jdbc:sqlite:" + DB_FILE_NAME;
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS contacts (" +
                    "id TEXT PRIMARY KEY," +
                    "firstName TEXT NOT NULL CHECK(length(firstName) >= 3 AND length(firstName) <= 50)," +
                    "lastName TEXT CHECK(lastName IS NULL OR length(lastName) <= 50)," +
                    "phoneNumber TEXT CHECK(phoneNumber IS NULL OR length(phoneNumber) <= 20)," +
                    "email TEXT CHECK(email IS NULL OR length(email) <= 100)," +
                    "address TEXT CHECK(address IS NULL OR length(address) <= 255)," +
                    "company TEXT CHECK(company IS NULL OR length(company) <= 100)," +
                    "birthDate TEXT," +
                    "notes TEXT CHECK(notes IS NULL OR length(notes) <= 255)," +
                    "createdAt TEXT NOT NULL," +
                    "updatedAt TEXT NOT NULL" +
                    ");";
            stmt.execute(sql);
            System.out.println("Database initialized (table 'contacts' checked/created).");

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void close(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception e) {
                System.err.println("Error closing resource: " + e.getMessage());
            }
        }
    }
}