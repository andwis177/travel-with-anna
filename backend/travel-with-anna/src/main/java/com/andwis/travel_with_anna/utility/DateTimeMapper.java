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
        return parseDateTime(dateTimeString, DATE_TIME_FORMATTER, "Invalid date-time format. Expected 'yyyy-MM-dd'T'HH:mm'");
    }

    public static @NotNull LocalDate toLocalDate(@NotNull String dateString) {
        return parseDateTime(dateString, DATE_FORMATTER, "Invalid date format. Expected 'yyyy-MM-dd'");
    }

    public static @Nullable LocalTime toTime(@NotNull String timeString) {
        try {
            return LocalTime.parse(timeString, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static <T> @NotNull T parseDateTime(String input, DateTimeFormatter formatter, String errorMessage) {
        try {
            if (formatter == DATE_TIME_FORMATTER) {
                return (T) LocalDateTime.parse(input, formatter);
            } else if (formatter == DATE_FORMATTER) {
                return (T) LocalDate.parse(input, formatter);
            } else {
                throw new IllegalArgumentException("Unsupported formatter provided");
            }
        } catch (DateTimeParseException e) {
            throw new DateTimeException(errorMessage, e);
        }
    }
}
