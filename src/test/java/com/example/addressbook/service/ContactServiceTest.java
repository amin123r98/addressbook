// src/test/java/com/example/addressbook/service/ContactServiceTest.java
package com.example.addressbook.service;

import com.example.addressbook.dao.ContactDao;
import com.example.addressbook.model.Contact;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private ContactDao contactDao;

    @InjectMocks
    private ContactService contactService;

    private Contact contact;

    @BeforeEach
    void setUp() {
        contact = new Contact();
        contact.setFirstName("John");
        contact.setLastName("Doe");
    }

    @Test
    void addContact_ShouldSetTimestampsAndCallDao() throws SQLException {
        // Arrange
        ArgumentCaptor<Contact> contactCaptor = ArgumentCaptor.forClass(Contact.class);

        // Act
        contactService.addContact(contact);

        // Assert
        // Проверяем, что метод DAO был вызван ровно один раз с захваченным аргументом
        verify(contactDao, times(1)).addContact(contactCaptor.capture());

        // Проверяем, что у переданного в DAO контакта установлены временные метки
        Contact capturedContact = contactCaptor.getValue();
        assertNotNull(capturedContact.getCreatedAt());
        assertNotNull(capturedContact.getUpdatedAt());
        assertEquals(capturedContact.getCreatedAt(), capturedContact.getUpdatedAt());
    }

    @Test
    void updateContact_ShouldSetUpdatedAtAndCallDao() throws SQLException {
        // Arrange
        contact.setCreatedAt(LocalDateTime.now().minusDays(1));
        ArgumentCaptor<Contact> contactCaptor = ArgumentCaptor.forClass(Contact.class);

        // Act
        contactService.updateContact(contact);

        // Assert
        verify(contactDao, times(1)).updateContact(contactCaptor.capture());

        Contact capturedContact = contactCaptor.getValue();
        assertNotNull(capturedContact.getUpdatedAt());
        assertTrue(capturedContact.getUpdatedAt().isAfter(capturedContact.getCreatedAt()));
    }

    @Test
    void deleteContact_ShouldCallDao() throws SQLException {
        // Arrange
        String contactId = "test-id";

        // Act
        contactService.deleteContact(contactId);

        // Assert
        verify(contactDao, times(1)).deleteContact(contactId);
    }

    @Test
    void getContacts_ShouldCallDaoAndReturnList() throws SQLException {
        // Arrange
        when(contactDao.getContacts(0, 10, "test"))
                .thenReturn(Collections.singletonList(contact));

        // Act
        List<Contact> result = contactService.getContacts(0, 10, "test");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
        verify(contactDao, times(1)).getContacts(0, 10, "test");
    }

    @Test
    void getTotalContactsCount_ShouldCallDaoAndReturnCount() throws SQLException {
        // Arrange
        when(contactDao.getTotalContactsCount("test")).thenReturn(5);

        // Act
        int count = contactService.getTotalContactsCount("test");

        // Assert
        assertEquals(5, count);
        verify(contactDao, times(1)).getTotalContactsCount("test");
    }
}