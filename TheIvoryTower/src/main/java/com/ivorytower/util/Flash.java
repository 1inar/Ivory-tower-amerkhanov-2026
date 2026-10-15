package com.ivorytower.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public final class Flash {
    private Flash() {}

    public static void set(HttpSession session, String type, String message) {
        session.setAttribute(Attr.FLASH_TYPE, type);
        session.setAttribute(Attr.FLASH_MSG, message);
    }

    public static void moveToRequest(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return;
        Object msg = session.getAttribute(Attr.FLASH_MSG);
        if (msg != null) {
            req.setAttribute(Attr.FLASH_TYPE, session.getAttribute(Attr.FLASH_TYPE));
            req.setAttribute(Attr.FLASH_MSG, msg);
            session.removeAttribute(Attr.FLASH_TYPE);
            session.removeAttribute(Attr.FLASH_MSG);
        }
    }
}