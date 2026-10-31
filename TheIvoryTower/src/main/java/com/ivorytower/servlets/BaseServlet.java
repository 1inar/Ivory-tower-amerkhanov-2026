package com.ivorytower.servlets;

import com.google.gson.Gson;
import com.ivorytower.exception.AccessDeniedException;
import com.ivorytower.exception.NotFoundException;
import com.ivorytower.model.User;
import com.ivorytower.util.Attr;
import com.ivorytower.util.Flash;
import com.ivorytower.util.ServiceRegistry;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public abstract class BaseServlet extends HttpServlet {
    private static final Gson GSON = new Gson();

    protected ServiceRegistry services() {
        return (ServiceRegistry) getServletContext().getAttribute(Attr.SERVICES);
    }

    protected User currentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (User) session.getAttribute(Attr.CURRENT_USER);
    }

    protected void forward(HttpServletRequest req, HttpServletResponse resp, String view)
            throws ServletException, IOException {
        Flash.moveToRequest(req);
        req.getRequestDispatcher("/WEB-INF/views/" + view).forward(req, resp);
    }

    protected void redirect(HttpServletRequest req, HttpServletResponse resp, String path)
            throws IOException {
        resp.sendRedirect(req.getContextPath() + path);
    }

    protected static String param(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return v == null ? "" : v.trim();
    }

    protected static int intParam(HttpServletRequest req, String name, int defaultValue) {
        try {
            return Integer.parseInt(param(req, name));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    protected static long longParam(HttpServletRequest req, String name) {
        try {
            return Long.parseLong(param(req, name));
        } catch (NumberFormatException e) {
            throw new NotFoundException("Неверный параметр " + name);
        }
    }

    protected void sendJson(HttpServletResponse resp, int status, Object body) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(GSON.toJson(body));
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            super.service(req, resp);
        } catch (NotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (AccessDeniedException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        }
    }
}