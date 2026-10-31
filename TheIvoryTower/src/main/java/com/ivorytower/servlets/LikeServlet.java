package com.ivorytower.servlets;

import com.ivorytower.model.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/api/like")
public class LikeServlet extends ApiServlet {
    @Override
    protected Object handle(HttpServletRequest req, User user) {
        return services().getReactionService().toggleLike(user.getId(), longParam(req, "id"));
    }
}