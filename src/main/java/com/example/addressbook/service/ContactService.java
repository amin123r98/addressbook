// src/main/java/com/example/addressbook/service/ContactService.java
package com.example.addressbook.service;

import com.example.addressbook.dao.ContactDao;
import com.example.addressbook.model.Contact;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ContactService {

    private final ContactDao contactDao;

    public ContactService() {
        this.contactDao = new ContactDao(); // В реальном приложении лучше использовать DI
    }

    // Конструктор для тестов
    public ContactService(ContactDao contactDao) {
        this.contactDao = contactDao;
    }

    public void addContact(Contact contact) throws SQLException {
        contact.setUpdatedAt(LocalDateTime.now());
        if (contact.getCreatedAt() == null) {
            contact.setCreatedAt(LocalDateTime.now());
        }
        contactDao.addContact(contact);
    }

    public Optional<Contact> getContactById(String id) throws SQLException {
        return contactDao.getContactById(id);
    }

    // Используется для основного отображения с пагинацией и поиском
    public List<Contact> getContacts(int pageNumber, int pageSize, String searchTerm) throws SQLException {
        return contactDao.getContacts(pageNumber, pageSize, searchTerm);
    }

    // Для получения общего числа контактов с учетом поиска
    public int getTotalContactsCount(String searchTerm) throws SQLException {
        return contactDao.getTotalContactsCount(searchTerm);
    }

    // Старый метод, если где-то еще используется без пагинации/поиска
    public List<Contact> getAllContacts() throws SQLException {
        return contactDao.getAllContacts();
    }

    public void updateContact(Contact contact) throws SQLException {
        contact.setUpdatedAt(LocalDateTime.now());
        contactDao.updateContact(contact);
    }

    public void deleteContact(String id) throws SQLException {
        contactDao.deleteContact(id);
    }
}