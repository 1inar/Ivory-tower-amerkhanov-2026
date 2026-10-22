package com.ivorytower.dao.jdbc;

import com.ivorytower.dao.ReviewDao;
import com.ivorytower.exception.DaoException;
import com.ivorytower.model.Review;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcReviewDao extends JdbcSupport implements ReviewDao {

    private static final String SELECT_BASE =
            "SELECT r.id, r.poem_id, r.critic_id, r.score, r.text, r.created_at, "
                    + "       u.username AS critic_name "
                    + "FROM reviews r JOIN users u ON u.id = r.critic_id ";

    public JdbcReviewDao(DataSource ds) {
        super(ds);
    }

    @Override
    public long create(Review r) {
        String sql = "INSERT INTO reviews (poem_id, critic_id, score, text) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, r.getPoemId());
            ps.setLong(2, r.getCriticId());
            ps.setInt(3, r.getScore());
            ps.setString(4, r.getText());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DaoException("Не удалось сохранить рецензию", e);
        }
    }

    @Override
    public List<Review> findByPoem(long poemId) {
        return query(SELECT_BASE + "WHERE r.poem_id = ? ORDER BY r.created_at DESC", poemId);
    }

    @Override
    public Optional<Review> findById(long id) {
        List<Review> list = query(SELECT_BASE + "WHERE r.id = ?", id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public boolean existsByPoemAndCritic(long poemId, long criticId) {
        return has("SELECT 1 FROM reviews WHERE poem_id = ? AND critic_id = ?", poemId, criticId);
    }

    @Override
    public boolean delete(long id) {
        return update("DELETE FROM reviews WHERE id = ?", id) > 0;
    }

    @Override
    public Double avgScore(long poemId) {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT avg(score) FROM reviews WHERE poem_id = ?")) {
            ps.setLong(1, poemId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                double v = rs.getDouble(1);
                return rs.wasNull() ? null : v;
            }
        } catch (SQLException e) {
            throw new DaoException("Не удалось посчитать среднюю оценку", e);
        }
    }

    private List<Review> query(String sql, Object... params) {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<Review> list = new ArrayList<>();
                while (rs.next()) {
                    Review r = new Review();
                    r.setId(rs.getLong("id"));
                    r.setPoemId(rs.getLong("poem_id"));
                    r.setCriticId(rs.getLong("critic_id"));
                    r.setScore(rs.getInt("score"));
                    r.setText(rs.getString("text"));
                    r.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    r.setCriticName(rs.getString("critic_name"));
                    list.add(r);
                }
                return list;
            }
        } catch (SQLException e) {
            throw new DaoException("Не удалось получить рецензии", e);
        }
    }
}