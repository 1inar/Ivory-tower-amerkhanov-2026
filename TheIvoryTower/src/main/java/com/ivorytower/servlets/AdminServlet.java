package com.ivorytower.servlets;

import com.ivorytower.exception.ServiceException;
import com.ivorytower.model.Role;
import com.ivorytower.model.User;
import com.ivorytower.util.Flash;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/admin")
public class AdminServlet extends BaseServlet {
    private static final int PAGE_SIZE = 15;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User admin = currentUser(req);
        int page = Math.max(1, intParam(req, "page", 1));

        req.setAttribute("users", services().getAdminService().users(admin, page, PAGE_SIZE));
        req.setAttribute("page", page);
        req.setAttribute("pages", services().getAdminService().pageCount(admin, PAGE_SIZE));
        forward(req, resp, "admin.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User admin = currentUser(req);
        String action = param(req, "action");
        long userId = longParam(req, "user");
        int page = Math.max(1, intParam(req, "page", 1));

        try {
            if ("role".equals(action)) {
                services().getAdminService().setRole(admin, userId, Role.valueOf(param(req, "role")));
                Flash.set(req.getSession(), "ok", "Роль изменена");
            } else if ("delete".equals(action)) {
                services().getAdminService().deleteUser(admin, userId);
                Flash.set(req.getSession(), "ok", "Пользователь удалён");
            } else {
                Flash.set(req.getSession(), "error", "Неизвестное действие");
            }
        } catch (IllegalArgumentException e) {
            Flash.set(req.getSession(), "error", "Неизвестная роль");
        } catch (ServiceException e) {
            Flash.set(req.getSession(), "error", e.getMessage());
        }
        redirect(req, resp, "/admin?page=" + page);
    }
}