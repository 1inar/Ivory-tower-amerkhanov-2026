package com.ivorytower.servlets;

import com.ivorytower.exception.NotFoundException;
import com.ivorytower.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/poet")
public class PoetServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long id = longParam(req, "id");
        User poet = services().getAuthService().findById(id)
                .orElseThrow(() -> new NotFoundException("Поэт не найден"));

        req.setAttribute("poet", poet);
        req.setAttribute("rating", services().getRatingService().of(id));
        req.setAttribute("poems", services().getPoemService().byAuthor(id));
        forward(req, resp, "poet.jsp");
    }
}