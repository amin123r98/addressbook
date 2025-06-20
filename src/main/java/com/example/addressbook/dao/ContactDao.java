// src/main/java/com/example/addressbook/dao/ContactDao.java
package com.example.addressbook.dao;

import com.example.addressbook.model.Contact;
import com.example.addressbook.util.DateUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ContactDao {

    public void addContact(Contact contact) throws SQLException {
        String sql = "INSERT INTO contacts (id, firstName, lastName, phoneNumber, email, address, company, birthDate, notes, createdAt, updatedAt) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, contact.getId());
            pstmt.setString(2, contact.getFirstName());
            pstmt.setString(3, contact.getLastName());
            pstmt.setString(4, contact.getPhoneNumber());
            pstmt.setString(5, contact.getEmail());
            pstmt.setString(6, contact.getAddress());
            pstmt.setString(7, contact.getCompany());
            pstmt.setString(8, DateUtil.format(contact.getBirthDate()));
            pstmt.setString(9, contact.getNotes());
            pstmt.setString(10, DateUtil.format(contact.getCreatedAt()));
            pstmt.setString(11, DateUtil.format(contact.getUpdatedAt()));

            pstmt.executeUpdate();
        }
    }

    public Optional<Contact> getContactById(String id) throws SQLException {
        String sql = "SELECT * FROM contacts WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToContact(rs));
            }
        }
        return Optional.empty();
    }

    // --- Методы для пагинации и поиска ---

    /**
     * Получает список контактов для указанной страницы и с учетом поискового запроса.
     * @param pageNumber Номер страницы (начиная с 0)
     * @param pageSize Размер страницы
     * @param searchTerm Строка для поиска по имени (может быть null или пустой)
     * @return Список контактов
     * @throws SQLException
     */
    public List<Contact> getContacts(int pageNumber, int pageSize, String searchTerm) throws SQLException {
        List<Contact> contacts = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM contacts ");
        List<Object> params = new ArrayList<>();

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sqlBuilder.append("WHERE lower(firstName) LIKE lower(?) "); // Поиск без учета регистра
            params.add("%" + searchTerm.trim() + "%");
        }

        sqlBuilder.append("ORDER BY firstName, lastName LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add(pageNumber * pageSize);

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                contacts.add(mapRowToContact(rs));
            }
        }
        return contacts;
    }

    /**
     * Получает общее количество контактов с учетом поискового запроса.
     * @param searchTerm Строка для поиска по имени (может быть null или пустой)
     * @return Общее количество контактов
     * @throws SQLException
     */
    public int getTotalContactsCount(String searchTerm) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder("SELECT COUNT(*) FROM contacts ");
        List<Object> params = new ArrayList<>();

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sqlBuilder.append("WHERE lower(firstName) LIKE lower(?)");
            params.add("%" + searchTerm.trim() + "%");
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    // Начальная версия getAllContacts без пагинации, поиска и сортировки
    // Оставляем его, если он где-то нужен, но для основного списка будем использовать getContacts
    public List<Contact> getAllContacts() throws SQLException {
        List<Contact> contacts = new ArrayList<>();
        String sql = "SELECT * FROM contacts ORDER BY firstName, lastName"; // Базовая сортировка
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                contacts.add(mapRowToContact(rs));
            }
        }
        return contacts;
    }


    public void updateContact(Contact contact) throws SQLException {
        String sql = "UPDATE contacts SET firstName = ?, lastName = ?, phoneNumber = ?, email = ?, " +
                "address = ?, company = ?, birthDate = ?, notes = ?, updatedAt = ? " +
                "WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, contact.getFirstName());
            pstmt.setString(2, contact.getLastName());
            pstmt.setString(3, contact.getPhoneNumber());
            pstmt.setString(4, contact.getEmail());
            pstmt.setString(5, contact.getAddress());
            pstmt.setString(6, contact.getCompany());
            pstmt.setString(7, DateUtil.format(contact.getBirthDate()));
            pstmt.setString(8, contact.getNotes());
            pstmt.setString(9, DateUtil.format(contact.getUpdatedAt())); // Обновляем updatedAt
            pstmt.setString(10, contact.getId());

            pstmt.executeUpdate();
        }
    }

    public void deleteContact(String id) throws SQLException {
        String sql = "DELETE FROM contacts WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        }
    }

    private Contact mapRowToContact(ResultSet rs) throws SQLException {
        return new Contact(
                rs.getString("id"),
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("phoneNumber"),
                rs.getString("email"),
                rs.getString("address"),
                rs.getString("company"),
                DateUtil.parseLocalDate(rs.getString("birthDate")),
                rs.getString("notes"),
                DateUtil.parseLocalDateTime(rs.getString("createdAt")),
                DateUtil.parseLocalDateTime(rs.getString("updatedAt"))
        );
    }
}