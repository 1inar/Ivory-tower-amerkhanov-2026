package com.ivorytower.servlets;

import com.ivorytower.exception.ServiceException;
import com.ivorytower.model.User;
import com.ivorytower.util.Attr;
import com.ivorytower.util.CsrfUtil;
import com.ivorytower.util.Flash;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

@WebServlet("/login")
public class LoginServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (currentUser(req) != null) {
            redirect(req, resp, "/");
            return;
        }
        req.setAttribute("formToken", CsrfUtil.newFormToken(req.getSession()));
        forward(req, resp, "login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!CsrfUtil.consumeFormToken(req)) {
            redirect(req, resp, "/login");
            return;
        }
        String email = param(req, "email");
        try {
            User user = services().getAuthService().login(email, req.getParameter("password"));

            HttpSession session = req.getSession();
            req.changeSessionId();                       // защита от фиксации сессии
            session.setAttribute(Attr.CURRENT_USER, user);
            Flash.set(session, "ok", "С возвращением, " + user.getUsername() + "!");

            String back = (String) session.getAttribute(Attr.RETURN_TO);
            session.removeAttribute(Attr.RETURN_TO);
            boolean safe = back != null && back.startsWith("/") && !back.startsWith("//");
            redirect(req, resp, safe ? back : "/");
        } catch (ServiceException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("old", Map.of("email", email));
            req.setAttribute("formToken", CsrfUtil.newFormToken(req.getSession()));
            forward(req, resp, "login.jsp");
        }
    }
}