// src/main/java/com/example/addressbook/model/Contact.java
package com.example.addressbook.model;

import javafx.beans.property.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Contact {

    private final StringProperty id;
    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty phoneNumber;
    private final StringProperty email;
    private final StringProperty address;
    private final StringProperty company;
    private final ObjectProperty<LocalDate> birthDate;
    private final StringProperty notes;
    private final ObjectProperty<LocalDateTime> createdAt;
    private final ObjectProperty<LocalDateTime> updatedAt;

    public Contact() {
        this(null, null, null, null, null, null, null, null, null, null, null);
    }

    public Contact(String id, String firstName, String lastName, String phoneNumber, String email,
                   String address, String company, LocalDate birthDate, String notes,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = new SimpleStringProperty(id == null ? UUID.randomUUID().toString() : id);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.email = new SimpleStringProperty(email);
        this.address = new SimpleStringProperty(address);
        this.company = new SimpleStringProperty(company);
        this.birthDate = new SimpleObjectProperty<>(birthDate);
        this.notes = new SimpleStringProperty(notes);
        this.createdAt = new SimpleObjectProperty<>(createdAt == null ? LocalDateTime.now() : createdAt);
        this.updatedAt = new SimpleObjectProperty<>(updatedAt == null ? LocalDateTime.now() : updatedAt);
    }

    // --- id ---
    public String getId() { return id.get(); }
    public StringProperty idProperty() { return id; }
    // setId не нужен, генерируется при создании

    // --- firstName ---
    public String getFirstName() { return firstName.get(); }
    public void setFirstName(String firstName) { this.firstName.set(firstName); }
    public StringProperty firstNameProperty() { return firstName; }

    // --- lastName ---
    public String getLastName() { return lastName.get(); }
    public void setLastName(String lastName) { this.lastName.set(lastName); }
    public StringProperty lastNameProperty() { return lastName; }

    // --- phoneNumber ---
    public String getPhoneNumber() { return phoneNumber.get(); }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber.set(phoneNumber); }
    public StringProperty phoneNumberProperty() { return phoneNumber; }

    // --- email ---
    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }
    public StringProperty emailProperty() { return email; }

    // --- address ---
    public String getAddress() { return address.get(); }
    public void setAddress(String address) { this.address.set(address); }
    public StringProperty addressProperty() { return address; }

    // --- company ---
    public String getCompany() { return company.get(); }
    public void setCompany(String company) { this.company.set(company); }
    public StringProperty companyProperty() { return company; }

    // --- birthDate ---
    public LocalDate getBirthDate() { return birthDate.get(); }
    public void setBirthDate(LocalDate birthDate) { this.birthDate.set(birthDate); }
    public ObjectProperty<LocalDate> birthDateProperty() { return birthDate; }

    // --- notes ---
    public String getNotes() { return notes.get(); }
    public void setNotes(String notes) { this.notes.set(notes); }
    public StringProperty notesProperty() { return notes; }

    // --- createdAt ---
    public LocalDateTime getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt.set(createdAt); }
    public ObjectProperty<LocalDateTime> createdAtProperty() { return createdAt; }

    // --- updatedAt ---
    public LocalDateTime getUpdatedAt() { return updatedAt.get(); }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt.set(updatedAt); }
    public ObjectProperty<LocalDateTime> updatedAtProperty() { return updatedAt; }

    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id.get() +
                ", firstName=" + firstName.get() +
                ", lastName=" + lastName.get() +
                '}';
    }
}