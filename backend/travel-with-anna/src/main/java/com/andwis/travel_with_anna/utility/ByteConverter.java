package com.andwis.travel_with_anna.utility;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class ByteConverter {

    private static final String HEX_FORMAT = "%02x";

    public static @NotNull String bytesToHex(byte @NotNull [] byteArray) {
        StringBuilder sb = new StringBuilder(byteArray.length * 2);
        for (byte b : byteArray) {
            sb.append(String.format(HEX_FORMAT, b));
        }
        return sb.toString();
    }

    public static byte @NotNull [] hexToBytes(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0) {
            throw new IllegalArgumentException("Invalid hex string. Must be non-null and have an even length.");
        }

        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[i / 2] = parseHexPairToByte(hexString.charAt(i), hexString.charAt(i + 1));
        }
        return bytes;
    }

    private static byte parseHexPairToByte(char high, char low) {
        return (byte) ((Character.digit(high, 16) << 4) + Character.digit(low, 16));
    }
}
