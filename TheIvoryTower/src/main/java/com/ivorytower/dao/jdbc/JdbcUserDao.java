package com.ivorytower.dao.jdbc;

import com.ivorytower.dao.UserDao;
import com.ivorytower.exception.DaoException;
import com.ivorytower.model.Role;
import com.ivorytower.model.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcUserDao extends JdbcSupport implements UserDao {

    public JdbcUserDao(DataSource ds) {
        super(ds);
    }

    @Override
    public long create(User u) {
        String sql = "INSERT INTO users (username, email, password_hash, role) "
                + "VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPasswordHash());
            ps.setString(4, u.getRole().name());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DaoException("Не удалось создать пользователя", e);
        }
    }

    @Override
    public Optional<User> findById(long id) {
        return findOne("SELECT * FROM users WHERE id = ?", id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return findOne("SELECT * FROM users WHERE email = ?", email);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return findOne("SELECT * FROM users WHERE username = ?", username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return exists("SELECT 1 FROM users WHERE email = ?", email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return exists("SELECT 1 FROM users WHERE username = ?", username);
    }

    @Override
    public void markEmailVerified(long id) {
        update("UPDATE users SET email_verified = TRUE WHERE id = ?", id);
    }

    @Override
    public void updateRole(long id, Role role) {
        update("UPDATE users SET role = ? WHERE id = ?", role.name(), id);
    }

    @Override
    public void updateBio(long id, String bio) {
        update("UPDATE users SET bio = ? WHERE id = ?", bio, id);
    }

    @Override
    public List<User> findAll(int limit, int offset) {
        String sql = "SELECT * FROM users ORDER BY id LIMIT ? OFFSET ?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                List<User> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(map(rs));
                }
                return list;
            }
        } catch (SQLException e) {
            throw new DaoException("Не удалось получить список пользователей", e);
        }
    }

    @Override
    public int count() {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT count(*) FROM users");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new DaoException("Не удалось посчитать пользователей", e);
        }
    }

    @Override
    public void delete(long id) {
        update("DELETE FROM users WHERE id = ?", id);
    }


    private Optional<User> findOne(String sql, Object param) {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Не удалось найти пользователя", e);
        }
    }

    private boolean exists(String sql, Object param) {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DaoException("Ошибка проверки существования пользователя", e);
        }
    }

    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setUsername(rs.getString("username"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRole(Role.valueOf(rs.getString("role")));
        u.setEmailVerified(rs.getBoolean("email_verified"));
        u.setBio(rs.getString("bio"));
        u.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        u.setCriticBlocked(rs.getBoolean("critic_blocked"));
        return u;
    }
    @Override
    public void updateCriticBlocked(long id, boolean blocked) {
        update("UPDATE users SET critic_blocked = ? WHERE id = ?", blocked, id);
    }
}