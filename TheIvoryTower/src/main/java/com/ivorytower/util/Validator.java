package com.ivorytower.util;

import java.util.regex.Pattern;

public final class Validator {
    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern USERNAME =
            Pattern.compile("^[\\p{L}0-9_]{3,30}$");

    private Validator() {}

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isEmail(String s) {
        return s != null && s.length() <= 120 && EMAIL.matcher(s).matches();
    }

    public static boolean isUsername(String s) {
        return s != null && USERNAME.matcher(s).matches();
    }

    public static boolean isStrongPassword(String s) {
        if (s == null || s.length() < 8 || s.length() > 72) return false;
        boolean letter = false, digit = false;
        for (char c : s.toCharArray()) {
            if (Character.isLetter(c)) letter = true;
            if (Character.isDigit(c)) digit = true;
        }
        return letter && digit;
    }

    public static boolean lengthBetween(String s, int min, int max) {
        if (s == null) return false;
        int len = s.trim().length();
        return len >= min && len <= max;
    }
}