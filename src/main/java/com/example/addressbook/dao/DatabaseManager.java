// src/main/java/com/example/addressbook/dao/DatabaseManager.java
package com.example.addressbook.dao;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String DB_FILE_NAME = "addressbook.db";
    private static String DB_URL; // Убрали final

    static {
        try {
            // Позволяем переопределить URL через системное свойство для тестов
            String testDbUrl = System.getProperty("db.url.test");
            if (testDbUrl != null && !testDbUrl.isEmpty()) {
                DB_URL = testDbUrl;
                System.out.println("Using test database URL: " + DB_URL);
            } else {
                String dbFilePath = Paths.get(DB_FILE_NAME).toAbsolutePath().toString();
                DB_URL = "jdbc:sqlite:" + dbFilePath;
                System.out.println("Database URL set to: " + DB_URL);
            }

            Class.forName("org.sqlite.JDBC");

        } catch (Exception e) {
            System.err.println("Could not determine database path or load driver: " + e.getMessage());
            e.printStackTrace();
            DB_URL = "jdbc:sqlite:" + DB_FILE_NAME; // Fallback
        }
    }

    public static Connection getConnection() throws SQLException {
        if (DB_URL == null) {
            // Этого не должно произойти, если статический блок отработал
            throw new SQLException("Database URL is not initialized.");
        }
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        // Перед инициализацией убедимся, что URL установлен (например, в тестах)
        if (DB_URL == null && System.getProperty("db.url.test") != null) {
            // Попытка повторной инициализации DB_URL, если тесты его задают, а статический блок еще не отработал
            // Это немного костыльно, лучше чтобы тесты устанавливали свойство ДО первого обращения к DatabaseManager
            String testDbUrl = System.getProperty("db.url.test");
            DB_URL = testDbUrl;
            System.out.println("Re-initialized test database URL for initializeDatabase(): " + DB_URL);
        }

        try (Connection conn = getConnection(); // Теперь будет использовать правильный DB_URL
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
            System.out.println("Database initialized (table 'contacts' checked/created) using URL: " + conn.getMetaData().getURL());

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
            // Дополнительная информация об URL при ошибке
            if (DB_URL != null) {
                System.err.println("Failed with DB_URL: " + DB_URL);
            }
        }
    }

    // close метод остается без изменений
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