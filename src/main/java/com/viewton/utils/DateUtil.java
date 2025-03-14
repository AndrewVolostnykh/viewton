package com.viewton.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// FIXME: to remove. Utils should be stored in separate library
@UtilityClass
public class DateUtil {

    public static LocalDateTime parseIsoDateTime(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        return LocalDateTime.parse(dateString, formatter);
    }

    public static LocalDate parseIsoDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
        return LocalDate.parse(dateString, formatter);
    }
}

