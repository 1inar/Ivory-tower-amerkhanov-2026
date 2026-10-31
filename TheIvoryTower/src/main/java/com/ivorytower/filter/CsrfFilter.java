package com.ivorytower.filter;

import com.ivorytower.util.Attr;
import com.ivorytower.util.CsrfUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class CsrfFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        if (req.getRequestURI().startsWith(req.getContextPath() + "/static/")) {
            chain.doFilter(req, resp);
            return;
        }
        if ("POST".equalsIgnoreCase(req.getMethod()) && !CsrfUtil.valid(req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Страница устарела. Обновите её и повторите.");
            return;
        }
        req.setAttribute(Attr.CSRF, CsrfUtil.token(req.getSession()));
        chain.doFilter(req, resp);
    }
}