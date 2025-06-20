// src/main/java/com/example/addressbook/util/ValidationUtil.java
package com.example.addressbook.util;

import java.util.regex.Pattern;

public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    // FR2.1.3 Валидация данных при создании/редактировании
    public static boolean isValidFirstName(String firstName) {
        return firstName != null && firstName.trim().length() >= 3 && firstName.trim().length() <= 50;
    }

    public static boolean isValidLastName(String lastName) {
        // Необязательное, но если заполнено, то до 50 символов
        return lastName == null || lastName.isEmpty() || lastName.trim().length() <= 50;
    }

    public static boolean isValidEmail(String email) {
        // Необязательное, но если заполнено, должно быть корректным
        return email == null || email.isEmpty() || EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        // Необязательное, базовая проверка на символы (цифры, +, -, (, ))
        // Можно усложнить, если нужно
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return true;
        }
        // Проверка на длину и символы
        String trimmedNumber = phoneNumber.trim();
        return trimmedNumber.length() <= 20 && trimmedNumber.matches("^[0-9()+\\- ]*$");
    }

    public static boolean isValidAddress(String address) {
        return address == null || address.isEmpty() || address.trim().length() <= 255;
    }

    public static boolean isValidCompany(String company) {
        return company == null || company.isEmpty() || company.trim().length() <= 100;
    }

    public static boolean isValidNotes(String notes) {
        return notes == null || notes.isEmpty() || notes.trim().length() <= 255;
    }
}