package com.ivorytower.dao.jdbc;

import com.ivorytower.dao.RatingDao;
import com.ivorytower.exception.DaoException;
import com.ivorytower.model.PoetRating;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcRatingDao extends JdbcSupport implements RatingDao {

    private static final String SELECT_RATING =
            "SELECT t.*, CAST(t.likes + t.highlights * 2 + round(coalesce(t.avg_review, 0) * 5) AS INTEGER) AS total "
                    + "FROM ( "
                    + "  SELECT u.id AS user_id, u.username, "
                    + "    (SELECT count(*) FROM poems p WHERE p.author_id = u.id) AS poem_count, "
                    + "    (SELECT count(*) FROM poem_likes l JOIN poems p ON p.id = l.poem_id "
                    + "       WHERE p.author_id = u.id) AS likes, "
                    + "    (SELECT count(*) FROM highlights h JOIN poems p ON p.id = h.poem_id "
                    + "       WHERE p.author_id = u.id) AS highlights, "
                    + "    (SELECT avg(r.score) FROM reviews r JOIN poems p ON p.id = r.poem_id "
                    + "       WHERE p.author_id = u.id) AS avg_review "
                    + "  FROM users u "
                    + ") t ";

    public JdbcRatingDao(DataSource ds) {
        super(ds);
    }

    @Override
    public List<PoetRating> top(int limit) {
        String sql = SELECT_RATING
                + "WHERE t.poem_count > 0 "
                + "ORDER BY total DESC, t.poem_count DESC, t.username "
                + "LIMIT ?";
        List<PoetRating> list = query(sql, limit);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setPosition(i + 1);
        }
        return list;
    }

    @Override
    public Optional<PoetRating> forUser(long userId) {
        List<PoetRating> list = query(SELECT_RATING + "WHERE t.user_id = ?", userId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public int poemCount(long userId) {
        return count("SELECT count(*) FROM poems WHERE author_id = ?", userId);
    }

    private List<PoetRating> query(String sql, Object... params) {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<PoetRating> list = new ArrayList<>();
                while (rs.next()) {
                    PoetRating r = new PoetRating();
                    r.setUserId(rs.getLong("user_id"));
                    r.setUsername(rs.getString("username"));
                    r.setPoemCount(rs.getInt("poem_count"));
                    r.setLikes(rs.getInt("likes"));
                    r.setHighlights(rs.getInt("highlights"));
                    double avg = rs.getDouble("avg_review");
                    r.setAvgReview(rs.wasNull() ? null : avg);
                    r.setTotalScore(rs.getInt("total"));
                    list.add(r);
                }
                return list;
            }
        } catch (SQLException e) {
            throw new DaoException("Не удалось получить рейтинг", e);
        }
    }
}