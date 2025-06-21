// src/test/java/com/example/addressbook/util/ValidationUtilTest.java
package com.example.addressbook.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilTest {

    @ParameterizedTest
    @ValueSource(strings = {"John", "Alexander", "Анна"})
    void isValidFirstName_ValidNames_ShouldReturnTrue(String firstName) {
        assertTrue(ValidationUtil.isValidFirstName(firstName));
    }

    @ParameterizedTest
    @ValueSource(strings = {"J", " ", "  ", "AReallyLongFirstNameThatExceedsTheFiftyCharacterLimit"})
    void isValidFirstName_InvalidNames_ShouldReturnFalse(String firstName) {
        assertFalse(ValidationUtil.isValidFirstName(firstName));
    }

    @Test
    void isValidFirstName_NullName_ShouldReturnFalse() {
        assertFalse(ValidationUtil.isValidFirstName(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Doe", "Smith-Jones", "", "Петров"})
    void isValidLastName_ValidNames_ShouldReturnTrue(String lastName) {
        assertTrue(ValidationUtil.isValidLastName(lastName));
    }

    @Test
    void isValidLastName_NullName_ShouldReturnTrue() {
        assertTrue(ValidationUtil.isValidLastName(null));
    }

    @Test
    void isValidLastName_TooLongName_ShouldReturnFalse() {
        String longName = "A".repeat(51);
        assertFalse(ValidationUtil.isValidLastName(longName));
    }

    @ParameterizedTest
    @CsvSource({
            "test@example.com, true",
            "test.name@example.co.uk, true",
            "user+alias@gmail.com, true",
            "'', true", // empty string is valid
            "test, false",
            "test@.com, false",
            "test@domain, false",
            "@domain.com, false"
    })
    void isValidEmail_VariousEmails_ShouldValidateCorrectly(String email, boolean expected) {
        // Handle special case for empty string literal from CsvSource
        if ("''".equals(email)) {
            email = "";
        }
        assertEquals(expected, ValidationUtil.isValidEmail(email));
    }

    @Test
    void isValidEmail_NullEmail_ShouldReturnTrue() {
        assertTrue(ValidationUtil.isValidEmail(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"+7(999)123-45-67", "1234567890", "123 456", ""})
    void isValidPhoneNumber_ValidNumbers_ShouldReturnTrue(String phone) {
        assertTrue(ValidationUtil.isValidPhoneNumber(phone));
    }

    @ParameterizedTest
    @ValueSource(strings = {"123-abc", "a-123", "123456789012345678901"}) // > 20 chars
    void isValidPhoneNumber_InvalidNumbers_ShouldReturnFalse(String phone) {
        assertFalse(ValidationUtil.isValidPhoneNumber(phone));
    }
}