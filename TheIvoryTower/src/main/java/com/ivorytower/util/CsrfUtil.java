package com.ivorytower.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class CsrfUtil {
    private CsrfUtil() {}

    public static String token(HttpSession session) {
        String token = (String) session.getAttribute(Attr.CSRF);
        if (token == null) {
            token = TokenUtil.randomToken();
            session.setAttribute(Attr.CSRF, token);
        }
        return token;
    }

    public static boolean valid(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return false;
        String expected = (String) session.getAttribute(Attr.CSRF);
        String actual = req.getParameter("_csrf");
        if (actual == null) actual = req.getHeader("X-CSRF-Token");
        if (expected == null || actual == null) return false;
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                actual.getBytes(StandardCharsets.UTF_8));
    }

    public static String newFormToken(HttpSession session) {
        String token = TokenUtil.randomToken();
        session.setAttribute(Attr.FORM_TOKEN, token);
        return token;
    }

    public static boolean consumeFormToken(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return false;
        String expected = (String) session.getAttribute(Attr.FORM_TOKEN);
        session.removeAttribute(Attr.FORM_TOKEN);
        String actual = req.getParameter("_form");
        return expected != null && expected.equals(actual);
    }
}