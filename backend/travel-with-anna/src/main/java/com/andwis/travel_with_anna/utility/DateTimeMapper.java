package com.andwis.travel_with_anna.utility;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeMapper {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static @NotNull LocalDateTime toLocalDateTime(@NotNull String dateTimeString) {
        return parseToLocalDateTime(dateTimeString);
    }

    public static @NotNull LocalDate toLocalDate(@NotNull String dateString) {
        return parseToLocalDate(dateString);
    }

    public static @Nullable LocalTime toTime(@NotNull String timeString) {
        try {
            return LocalTime.parse(timeString, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static @NotNull LocalDateTime parseToLocalDateTime(String input) {
        try {
            return LocalDateTime.parse(input, DateTimeMapper.DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new DateTimeException("Invalid date-time format. Expected 'yyyy-MM-dd'T'HH:mm'", e);
        }
    }

    private static @NotNull LocalDate parseToLocalDate(String input) {
        try {
            return LocalDate.parse(input, DateTimeMapper.DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new DateTimeException("Invalid date format. Expected 'yyyy-MM-dd'", e);
        }
    }
}
