package com.ivorytower.servlets;

import com.ivorytower.model.User;
import com.ivorytower.util.Flash;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/poem/delete")
public class PoemDeleteServlet extends BaseServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = currentUser(req);
        services().getPoemService().delete(user, longParam(req, "id"));
        Flash.set(req.getSession(), "ok", "Стих удалён");
        redirect(req, resp, "/poet?id=" + user.getId());
    }
}