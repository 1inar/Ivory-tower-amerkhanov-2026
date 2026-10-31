package com.ivorytower.servlets;

import com.ivorytower.util.Flash;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/review/delete")
public class ReviewDeleteServlet extends BaseServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        services().getReviewService().delete(currentUser(req), longParam(req, "id"));
        Flash.set(req.getSession(), "ok", "Рецензия удалена");
        redirect(req, resp, "/poem?id=" + longParam(req, "poem"));
    }
}