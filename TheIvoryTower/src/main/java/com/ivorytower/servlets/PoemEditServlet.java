package com.ivorytower.servlets;

import com.ivorytower.exception.AccessDeniedException;
import com.ivorytower.exception.ValidationException;
import com.ivorytower.model.Poem;
import com.ivorytower.model.Tag;
import com.ivorytower.model.User;
import com.ivorytower.util.CsrfUtil;
import com.ivorytower.util.Flash;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/poem/edit")
public class PoemEditServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long id = longParam(req, "id");
        User user = currentUser(req);
        Poem poem = services().getPoemService().view(id, user);
        if (!user.getId().equals(poem.getAuthorId()) && !user.isAdmin()) {
            throw new AccessDeniedException("Изменять стих может только его автор");
        }
        String tags = poem.getTags().stream().map(Tag::getName).collect(Collectors.joining(", "));
        showForm(req, resp, id, Map.of("title", poem.getTitle(), "body", poem.getBody(), "tags", tags));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long id = longParam(req, "id");
        if (!CsrfUtil.consumeFormToken(req)) {
            redirect(req, resp, "/poem?id=" + id);
            return;
        }
        String title = param(req, "title");
        String body = req.getParameter("body") == null ? "" : req.getParameter("body");
        String tags = param(req, "tags");
        try {
            services().getPoemService().update(currentUser(req), id, title, body, tags);
            Flash.set(req.getSession(), "ok", "Изменения сохранены");
            redirect(req, resp, "/poem?id=" + id);
        } catch (ValidationException e) {
            req.setAttribute("errors", e.getErrors());
            showForm(req, resp, id, Map.of("title", title, "body", body, "tags", tags));
        }
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, long id, Map<String, String> old)
            throws ServletException, IOException {
        req.setAttribute("old", old);
        req.setAttribute("heading", "Редактирование стиха");
        req.setAttribute("formAction", "/poem/edit?id=" + id);
        req.setAttribute("formToken", CsrfUtil.newFormToken(req.getSession()));
        forward(req, resp, "poem-form.jsp");
    }
}