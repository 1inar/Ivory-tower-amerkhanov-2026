package com.ivorytower.dao.jdbc;

import com.ivorytower.dao.TagDao;
import com.ivorytower.exception.DaoException;
import com.ivorytower.model.Tag;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcTagDao extends JdbcSupport implements TagDao {

    public JdbcTagDao(DataSource ds) {
        super(ds);
    }

    @Override
    public Tag findOrCreate(String name) {
        String sql = "INSERT INTO tags (name) VALUES (?) "
                + "ON CONFLICT (name) DO UPDATE SET name = EXCLUDED.name "
                + "RETURNING id, name";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return new Tag(rs.getInt("id"), rs.getString("name"));
            }
        } catch (SQLException e) {
            throw new DaoException("Не удалось создать тег", e);
        }
    }

    @Override
    public List<Tag> findAll() {
        return query("SELECT id, name FROM tags ORDER BY name");
    }

    @Override
    public List<Tag> findByPoem(long poemId) {
        return query("SELECT t.id, t.name FROM tags t "
                + "JOIN poem_tags pt ON pt.tag_id = t.id "
                + "WHERE pt.poem_id = ? ORDER BY t.name", poemId);
    }

    private List<Tag> query(String sql, Object... params) {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<Tag> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new Tag(rs.getInt("id"), rs.getString("name")));
                }
                return list;
            }
        } catch (SQLException e) {
            throw new DaoException("Не удалось получить теги", e);
        }
    }
}