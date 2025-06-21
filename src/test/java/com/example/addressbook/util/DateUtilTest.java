// src/test/java/com/example/addressbook/util/DateUtilTest.java
package com.example.addressbook.util;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class DateUtilTest {

    @Test
    void format_LocalDate_ShouldFormatCorrectly() {
        LocalDate date = LocalDate.of(2023, 10, 27);
        assertEquals("2023-10-27", DateUtil.format(date));
    }

    @Test
    void format_NullLocalDate_ShouldReturnNull() {
        assertNull(DateUtil.format((LocalDate) null));
    }

    @Test
    void format_LocalDateTime_ShouldFormatCorrectly() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 10, 27, 15, 30, 0);
        assertEquals("2023-10-27 15:30:00", DateUtil.format(dateTime));
    }

    @Test
    void format_NullLocalDateTime_ShouldReturnNull() {
        assertNull(DateUtil.format((LocalDateTime) null));
    }

    @Test
    void parseLocalDate_ValidString_ShouldParseCorrectly() {
        String dateString = "2023-10-27";
        LocalDate expected = LocalDate.of(2023, 10, 27);
        assertEquals(expected, DateUtil.parseLocalDate(dateString));
    }

    @Test
    void parseLocalDate_NullOrEmptyString_ShouldReturnNull() {
        assertNull(DateUtil.parseLocalDate(null));
        assertNull(DateUtil.parseLocalDate(""));
    }

    @Test
    void parseLocalDateTime_ValidString_ShouldParseCorrectly() {
        String dateTimeString = "2023-10-27 15:30:00";
        LocalDateTime expected = LocalDateTime.of(2023, 10, 27, 15, 30, 0);
        assertEquals(expected, DateUtil.parseLocalDateTime(dateTimeString));
    }

    @Test
    void parseLocalDateTime_NullOrEmptyString_ShouldReturnNull() {
        assertNull(DateUtil.parseLocalDateTime(null));
        assertNull(DateUtil.parseLocalDateTime(""));
    }
}