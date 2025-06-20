// src/main/java/com/example/addressbook/dao/ContactDao.java
package com.example.addressbook.dao;

import com.example.addressbook.model.Contact;
import com.example.addressbook.util.DateUtil; // Создадим позже

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

    // Начальная версия getAllContacts без пагинации, поиска и сортировки
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

    // Вспомогательный метод для маппинга ResultSet -> Contact
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