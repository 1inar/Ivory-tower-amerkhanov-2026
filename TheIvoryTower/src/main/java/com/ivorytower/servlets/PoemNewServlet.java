package com.ivorytower.servlets;

import com.ivorytower.exception.ValidationException;
import com.ivorytower.model.User;
import com.ivorytower.util.CsrfUtil;
import com.ivorytower.util.Flash;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/poem/new")
public class PoemNewServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        showForm(req, resp, Map.of("title", "", "body", "", "tags", ""));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!CsrfUtil.consumeFormToken(req)) {
            redirect(req, resp, "/poem/new");
            return;
        }
        User user = currentUser(req);
        String title = param(req, "title");
        String body = req.getParameter("body") == null ? "" : req.getParameter("body");
        String tags = param(req, "tags");
        try {
            long id = services().getPoemService().create(user, title, body, tags);
            Flash.set(req.getSession(), "ok", "Стих опубликован");
            redirect(req, resp, "/poem?id=" + id);
        } catch (ValidationException e) {
            req.setAttribute("errors", e.getErrors());
            showForm(req, resp, Map.of("title", title, "body", body, "tags", tags));
        }
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Map<String, String> old)
            throws ServletException, IOException {
        req.setAttribute("old", old);
        req.setAttribute("heading", "Новый стих");
        req.setAttribute("formAction", "/poem/new");
        req.setAttribute("formToken", CsrfUtil.newFormToken(req.getSession()));
        forward(req, resp, "poem-form.jsp");
    }
}