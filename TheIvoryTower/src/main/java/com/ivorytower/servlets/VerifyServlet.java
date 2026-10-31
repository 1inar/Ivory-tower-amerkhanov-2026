package com.ivorytower.servlets;

import com.ivorytower.exception.ServiceException;
import com.ivorytower.util.Flash;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/verify")
public class VerifyServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            services().getAuthService().confirmEmail(req.getParameter("token"));
            Flash.set(req.getSession(), "ok", "Почта подтверждена. Теперь можно войти.");
        } catch (ServiceException e) {
            Flash.set(req.getSession(), "error", e.getMessage());
        }
        redirect(req, resp, "/login");
    }
}