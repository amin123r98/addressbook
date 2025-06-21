// src/test/java/com/example/addressbook/dao/ContactDaoTest.java
package com.example.addressbook.dao;

import com.example.addressbook.model.Contact;
import org.junit.jupiter.api.*;

import java.sql.Connection; // Можно удалить, если не используется напрямую
import java.sql.DriverManager; // Можно удалить
import java.sql.SQLException;
import java.sql.Statement; // Можно удалить, если не используется напрямую
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContactDaoTest {

    private ContactDao contactDao;

    @BeforeAll
    void setupShared() {
        // Устанавливаем системное свойство ДО ПЕРВОГО обращения к DatabaseManager
        System.setProperty("db.url.test", "jdbc:sqlite::memory:?cache=shared"); // <--- ДОБАВЬТЕ ?cache=shared
        System.out.println("Test property 'db.url.test' set to jdbc:sqlite::memory:?cache=shared");

        contactDao = new ContactDao();
        // DatabaseManager.initializeDatabase(); // УБИРАЕМ ОТСЮДА, будем делать в @BeforeEach
    }

    @BeforeEach
    void setupDatabaseForEachTest() throws SQLException {
        // Инициализируем (создаем таблицу) перед каждым тестом
        // Это гарантирует, что таблица существует для каждого теста
        // и что тесты не влияют друг на друга через состояние БД от предыдущего теста.
        DatabaseManager.initializeDatabase(); // <--- СОЗДАЕМ ТАБЛИЦУ ЗДЕСЬ

        // Очищаем таблицу (хотя если она создается заново каждый раз, это может быть избыточно,
        // но оставим для явного разделения тестов, если вдруг initializeDatabase не удаляет старую)
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM contacts");
        }
    }

    @AfterAll
    void tearDownDatabaseForAllTests() {
        System.clearProperty("db.url.test");
        // Можно попытаться закрыть соединение, если бы оно удерживалось.
        // С cache=shared и DriverManager, управление соединениями сложнее.
        // Обычно для :memory: это не так критично.
    }

    // ... ваши тесты ...
}