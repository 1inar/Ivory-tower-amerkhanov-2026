package com.ivorytower.servlets;

import com.ivorytower.exception.ServiceException;
import com.ivorytower.exception.ValidationException;
import com.ivorytower.model.User;
import com.ivorytower.util.CsrfUtil;
import com.ivorytower.util.Flash;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/profile/edit")
public class ProfileEditServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String bio = currentUser(req).getBio();
        showForm(req, resp, bio == null ? "" : bio);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!CsrfUtil.consumeFormToken(req)) {
            redirect(req, resp, "/profile/edit");
            return;
        }
        User user = currentUser(req);
        String bio = req.getParameter("bio") == null ? "" : req.getParameter("bio");
        try {
            services().getAuthService().updateBio(user, bio);
            Flash.set(req.getSession(), "ok", "Профиль обновлён");
            redirect(req, resp, "/poet?id=" + user.getId());
        } catch (ValidationException e) {
            req.setAttribute("error", String.join("; ", e.getErrors().values()));
            showForm(req, resp, bio);
        } catch (ServiceException e) {
            req.setAttribute("error", e.getMessage());
            showForm(req, resp, bio);
        }
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, String bio)
            throws ServletException, IOException {
        req.setAttribute("bio", bio);
        req.setAttribute("formToken", CsrfUtil.newFormToken(req.getSession()));
        forward(req, resp, "profile-edit.jsp");
    }
}