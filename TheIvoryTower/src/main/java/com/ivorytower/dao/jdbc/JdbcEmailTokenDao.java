package com.ivorytower.dao.jdbc;

import com.ivorytower.dao.EmailTokenDao;
import com.ivorytower.exception.DaoException;
import com.ivorytower.model.EmailToken;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

public class JdbcEmailTokenDao extends JdbcSupport implements EmailTokenDao {

    public JdbcEmailTokenDao(DataSource ds) {
        super(ds);
    }

    @Override
    public void save(EmailToken t) {
        update("INSERT INTO email_tokens (user_id, token, expires_at) VALUES (?, ?, ?)",
                t.getUserId(), t.getToken(), Timestamp.valueOf(t.getExpiresAt()));
    }

    @Override
    public Optional<EmailToken> findByToken(String token) {
        String sql = "SELECT id, user_id, token, expires_at FROM email_tokens WHERE token = ?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                EmailToken t = new EmailToken();
                t.setId(rs.getLong("id"));
                t.setUserId(rs.getLong("user_id"));
                t.setToken(rs.getString("token"));
                t.setExpiresAt(rs.getTimestamp("expires_at").toLocalDateTime());
                return Optional.of(t);
            }
        } catch (SQLException e) {
            throw new DaoException("Не удалось найти токен", e);
        }
    }

    @Override
    public void deleteByToken(String token) {
        update("DELETE FROM email_tokens WHERE token = ?", token);
    }

    @Override
    public void deleteExpired() {
        update("DELETE FROM email_tokens WHERE expires_at < now()");
    }
}