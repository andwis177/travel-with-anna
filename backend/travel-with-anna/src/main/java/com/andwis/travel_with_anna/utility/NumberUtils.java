package com.andwis.travel_with_anna.utility;

import java.util.Optional;

public class NumberUtils {

    public static boolean isValidLong(String str) {
        return Optional.ofNullable(str)
                .map(NumberUtils::tryParseLong)
                .orElse(false);
    }

    private static boolean tryParseLong(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}