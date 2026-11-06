package com.ivorytower.servlets;

import com.ivorytower.exception.ServiceException;
import com.ivorytower.exception.ValidationException;
import com.ivorytower.util.CsrfUtil;
import com.ivorytower.util.Flash;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/register")
public class RegisterServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (currentUser(req) != null) {
            redirect(req, resp, "/");
            return;
        }
        req.setAttribute("formToken", CsrfUtil.newFormToken(req.getSession()));
        forward(req, resp, "register.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!CsrfUtil.consumeFormToken(req)) {
            redirect(req, resp, "/register");
            return;
        }
        String username = param(req, "username");
        String email = param(req, "email");
        try {
            services().getAuthService().register(username, email,
                    req.getParameter("password"), req.getParameter("password2"));
            Flash.set(req.getSession(), "ok", "Регистрация прошла успешно. Теперь можно войти.");
            redirect(req, resp, "/login");
        } catch (ValidationException e) {
            req.setAttribute("errors", e.getErrors());
            showFormAgain(req, resp, username, email);
        } catch (ServiceException e) {
            req.setAttribute("error", e.getMessage());
            showFormAgain(req, resp, username, email);
        }
    }

    private void showFormAgain(HttpServletRequest req, HttpServletResponse resp, String username, String email)
            throws ServletException, IOException {
        req.setAttribute("old", Map.of("username", username, "email", email));
        req.setAttribute("formToken", CsrfUtil.newFormToken(req.getSession()));
        forward(req, resp, "register.jsp");
    }
}