// src/main/java/com/example/addressbook/util/DateUtil.java
package com.example.addressbook.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtil {

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss"; // SQLite обычно не хранит миллисекунды

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    public static String format(LocalDate date) {
        if (date == null) {
            return null;
        }
        return DATE_FORMATTER.format(date);
    }

    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return DATE_TIME_FORMATTER.format(dateTime);
    }

    public static LocalDate parseLocalDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateString, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            System.err.println("Error parsing LocalDate: " + dateString + " - " + e.getMessage());
            return null;
        }
    }

    public static LocalDateTime parseLocalDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return null;
        }
        try {
            // Попытка парсинга с полным форматом
            return LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e1) {
            // SQLite может вернуть дату без времени если так было сохранено, или если формат был YYYY-MM-DDTHH:MM:SS
            // Пробуем обработать другие распространенные форматы или убрать 'T'
            try {
                return LocalDateTime.parse(dateTimeString.replace("T", " "), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException e2) {
                // Если и это не помогло, и строка содержит только дату
                try {
                    LocalDate ld = LocalDate.parse(dateTimeString, DateTimeFormatter.ISO_LOCAL_DATE);
                    return ld.atStartOfDay();
                } catch (DateTimeParseException e3) {
                    System.err.println("Error parsing LocalDateTime: " + dateTimeString + " - " + e3.getMessage());
                    return null; // или выбросить исключение / вернуть дефолтное значение
                }
            }
        }
    }
}