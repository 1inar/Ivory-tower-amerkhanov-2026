package com.ivorytower.filter;

import com.ivorytower.model.User;
import com.ivorytower.util.Attr;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class RoleFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute(Attr.CURRENT_USER);
        String path = req.getServletPath();

        boolean allowed = true;
        if (path.startsWith("/admin")) {
            allowed = user != null && user.isAdmin();
        } else if (path.startsWith("/review")) {
            allowed = user != null && user.isReviewer();
        }
        if (!allowed) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "У вас нет доступа к этой странице");
            return;
        }
        chain.doFilter(req, resp);
    }
}