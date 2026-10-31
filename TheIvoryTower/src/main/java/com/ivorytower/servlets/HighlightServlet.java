package com.ivorytower.servlets;

import com.ivorytower.model.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/api/highlight")
public class HighlightServlet extends ApiServlet {
    @Override
    protected Object handle(HttpServletRequest req, User user) {
        return services().getHighlightService()
                .toggle(user.getId(), longParam(req, "id"), intParam(req, "line", 0));
    }
}