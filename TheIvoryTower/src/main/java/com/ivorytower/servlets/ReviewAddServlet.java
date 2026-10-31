package com.ivorytower.servlets;

import com.ivorytower.exception.ServiceException;
import com.ivorytower.exception.ValidationException;
import com.ivorytower.util.Flash;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/review/add")
public class ReviewAddServlet extends BaseServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long poemId = longParam(req, "poem");
        try {
            services().getReviewService().add(currentUser(req), poemId,
                    intParam(req, "score", 0), param(req, "text"));
            Flash.set(req.getSession(), "ok", "Рецензия опубликована");
        } catch (ValidationException e) {
            Flash.set(req.getSession(), "error", String.join("; ", e.getErrors().values()));
        } catch (ServiceException e) {
            Flash.set(req.getSession(), "error", e.getMessage());
        }
        redirect(req, resp, "/poem?id=" + poemId);
    }
}