package com.ivorytower.servlets;

import com.ivorytower.model.Poem;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"", "/home"})
public class HomeServlet extends BaseServlet {
    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String q = param(req, "q");
        String tag = param(req, "tag");
        int page = q.isEmpty() ? Math.max(1, intParam(req, "page", 1)) : 1;

        List<Poem> poems;
        if (!q.isEmpty()) {
            poems = services().getPoemService().search(q);
        } else if (!tag.isEmpty()) {
            poems = services().getPoemService().byTag(tag, page, PAGE_SIZE);
        } else {
            poems = services().getPoemService().latest(page, PAGE_SIZE);
        }

        req.setAttribute("poems", poems);
        req.setAttribute("q", q);
        req.setAttribute("tag", tag);
        req.setAttribute("page", page);
        req.setAttribute("hasPrev", page > 1);
        req.setAttribute("hasNext", q.isEmpty() && poems.size() == PAGE_SIZE);
        req.setAttribute("quotes", services().getHighlightService().quoteOfTower(5));
        forward(req, resp, "home.jsp");
    }
}