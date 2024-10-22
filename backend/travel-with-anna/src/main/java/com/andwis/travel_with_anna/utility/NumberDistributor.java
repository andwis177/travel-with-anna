package com.andwis.travel_with_anna.utility;

public class NumberDistributor {
    private static int number = 1;

    public static int getNumber() {
        return number++;
    }

    public static void reset() {
        number = 1;
    }
}
