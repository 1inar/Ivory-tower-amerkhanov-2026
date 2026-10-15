package com.ivorytower.util;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordUtil {
    private PasswordUtil() {}

    public static String hash(String raw) {
        return BCrypt.hashpw(raw, BCrypt.gensalt(10));
    }

    public static boolean check(String raw, String hash) {
        try {
            return BCrypt.checkpw(raw, hash);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}