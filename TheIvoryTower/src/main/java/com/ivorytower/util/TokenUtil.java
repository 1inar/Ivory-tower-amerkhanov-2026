package com.ivorytower.util;

import java.security.SecureRandom;
import java.util.HexFormat;

public final class TokenUtil {
    private static final SecureRandom RANDOM = new SecureRandom();

    private TokenUtil() {}

    public static String randomToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }
}