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
        // id, createdAt, updatedAt устанавливаются в конструкторе Contact
        // но updatedAt можно обновить здесь еще раз на всякий случай
        contact.setUpdatedAt(LocalDateTime.now());
        if (contact.getCreatedAt() == null) { // Если вдруг не установлен
            contact.setCreatedAt(LocalDateTime.now());
        }
        contactDao.addContact(contact);
    }

    public Optional<Contact> getContactById(String id) throws SQLException {
        return contactDao.getContactById(id);
    }

    public List<Contact> getAllContacts() throws SQLException {
        return contactDao.getAllContacts(); // Пока без параметров
    }

    public void updateContact(Contact contact) throws SQLException {
        contact.setUpdatedAt(LocalDateTime.now()); // Обновляем дату изменения
        contactDao.updateContact(contact);
    }

    public void deleteContact(String id) throws SQLException {
        contactDao.deleteContact(id);
    }

    // Методы для пагинации, поиска и сортировки будут добавлены позже
    // Например:
    // public List<Contact> getContacts(int pageNumber, int pageSize, String searchTerm, String sortBy, String filterBy)
    // public int getTotalContactsCount(String searchTerm, String filterBy)
}