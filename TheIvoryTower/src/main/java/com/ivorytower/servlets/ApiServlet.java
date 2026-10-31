package com.ivorytower.servlets;

import com.ivorytower.exception.ServiceException;
import com.ivorytower.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

public abstract class ApiServlet extends BaseServlet {

    protected abstract Object handle(HttpServletRequest req, User user);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            sendJson(resp, HttpServletResponse.SC_OK, handle(req, currentUser(req)));
        } catch (ServiceException e) {
            sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, Map.of("error", e.getMessage()));
        }
    }
}