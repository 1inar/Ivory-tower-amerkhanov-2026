package com.ivorytower.servlets;

import com.ivorytower.model.LineStat;
import com.ivorytower.model.Poem;
import com.ivorytower.model.Review;
import com.ivorytower.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/poem")
public class PoemServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long id = longParam(req, "id");
        User user = currentUser(req);
        Poem poem = services().getPoemService().view(id, user);


        Long viewerId = user == null ? null : user.getId();
        Map<Integer, LineStat> stats = new HashMap<>();
        for (LineStat s : services().getHighlightService().stats(id, viewerId)) {
            stats.put(s.getLineNo(), s);
        }

        List<Review> reviews = services().getReviewService().forPoem(id);
        boolean canReact = user != null && !user.getId().equals(poem.getAuthorId());
        boolean canReview = canReact && user.isReviewer()
                && reviews.stream().noneMatch(r -> r.getCriticId().equals(user.getId()));

        req.setAttribute("poem", poem);
        req.setAttribute("stats", stats);
        req.setAttribute("reviews", reviews);
        req.setAttribute("canReact", canReact);
        req.setAttribute("canReview", canReview);
        forward(req, resp, "poem.jsp");
    }
}