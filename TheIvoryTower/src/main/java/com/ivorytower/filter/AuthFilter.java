package com.ivorytower.filter;

import com.ivorytower.model.User;
import com.ivorytower.service.AuthService;
import com.ivorytower.util.Attr;
import com.ivorytower.util.ServiceRegistry;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

public class AuthFilter implements Filter {
    private AuthService authService;

    @Override
    public void init(FilterConfig config) {
        ServiceRegistry registry = (ServiceRegistry) config.getServletContext().getAttribute(Attr.SERVICES);
        authService = registry.getAuthService();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute(Attr.CURRENT_USER);

        if (user != null) {
            Optional<User> fresh = authService.findById(user.getId());
            if (fresh.isPresent()) {
                user = fresh.get();
                session.setAttribute(Attr.CURRENT_USER, user);
            } else {
                session.invalidate();
                user = null;
            }
        }

        if (user == null) {
            if (req.getServletPath().startsWith("/api/")) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                resp.getWriter().write("{\"error\":\"Войдите в аккаунт\"}");
                return;
            }
            if ("GET".equalsIgnoreCase(req.getMethod())) {
                String path = req.getRequestURI().substring(req.getContextPath().length());
                String query = req.getQueryString();
                req.getSession().setAttribute(Attr.RETURN_TO, query == null ? path : path + "?" + query);
            }
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        chain.doFilter(req, resp);
    }
}