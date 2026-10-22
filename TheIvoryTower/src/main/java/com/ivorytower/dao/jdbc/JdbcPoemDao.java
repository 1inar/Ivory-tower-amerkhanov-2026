package com.ivorytower.dao.jdbc;

import com.ivorytower.dao.PoemDao;
import com.ivorytower.exception.DaoException;
import com.ivorytower.model.Poem;
import com.ivorytower.model.Tag;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JdbcPoemDao extends JdbcSupport implements PoemDao {

    /** Общая часть всех выборок: стих + имя автора + число лайков + средняя оценка. */
    private static final String SELECT_BASE =
            "SELECT p.id, p.author_id, p.title, p.body, p.created_at, p.updated_at, "
                    + "       u.username AS author_name, "
                    + "       (SELECT count(*) FROM poem_likes l WHERE l.poem_id = p.id) AS like_count, "
                    + "       (SELECT avg(r.score) FROM reviews r WHERE r.poem_id = p.id) AS avg_score "
                    + "FROM poems p JOIN users u ON u.id = p.author_id ";

    private static final String ORDER_LATEST = " ORDER BY p.created_at DESC, p.id DESC ";

    public JdbcPoemDao(DataSource ds) {
        super(ds);
    }

    @Override
    public long create(Poem poem, List<Integer> tagIds) {
        try (Connection c = ds.getConnection()) {
            c.setAutoCommit(false);
            try {
                long id;
                String sql = "INSERT INTO poems (author_id, title, body) VALUES (?, ?, ?) RETURNING id";
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setLong(1, poem.getAuthorId());
                    ps.setString(2, poem.getTitle());
                    ps.setString(3, poem.getBody());
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        id = rs.getLong(1);
                    }
                }
                linkTags(c, id, tagIds);
                c.commit();
                return id;
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DaoException("Не удалось создать стих", e);
        }
    }

    @Override
    public boolean update(Poem poem, List<Integer> tagIds) {
        try (Connection c = ds.getConnection()) {
            c.setAutoCommit(false);
            try {
                int rows;
                String sql = "UPDATE poems SET title = ?, body = ?, updated_at = now() WHERE id = ?";
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setString(1, poem.getTitle());
                    ps.setString(2, poem.getBody());
                    ps.setLong(3, poem.getId());
                    rows = ps.executeUpdate();
                }
                if (rows == 0) {
                    c.rollback();
                    return false;
                }
                try (PreparedStatement ps = c.prepareStatement("DELETE FROM poem_tags WHERE poem_id = ?")) {
                    ps.setLong(1, poem.getId());
                    ps.executeUpdate();
                }
                linkTags(c, poem.getId(), tagIds);
                c.commit();
                return true;
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DaoException("Не удалось обновить стих", e);
        }
    }

    @Override
    public boolean delete(long id) {
        return update("DELETE FROM poems WHERE id = ?", id) > 0;
    }


    @Override
    public Optional<Poem> findById(long id) {
        List<Poem> list = query(SELECT_BASE + "WHERE p.id = ?", id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public List<Poem> findLatest(int limit, int offset) {
        return query(SELECT_BASE + ORDER_LATEST + "LIMIT ? OFFSET ?", limit, offset);
    }

    @Override
    public List<Poem> findByAuthor(long authorId) {
        return query(SELECT_BASE + "WHERE p.author_id = ?" + ORDER_LATEST, authorId);
    }

    @Override
    public List<Poem> findByTag(String tagName, int limit, int offset) {
        return query(SELECT_BASE
                + "WHERE EXISTS (SELECT 1 FROM poem_tags pt JOIN tags t ON t.id = pt.tag_id "
                + "              WHERE pt.poem_id = p.id AND t.name = ?)"
                + ORDER_LATEST + "LIMIT ? OFFSET ?", tagName, limit, offset);
    }

    @Override
    public List<Poem> search(String queryText) {
        String like = "%" + queryText + "%";
        return query(SELECT_BASE
                + "WHERE p.title ILIKE ? OR p.body ILIKE ? OR u.username ILIKE ?"
                + ORDER_LATEST + "LIMIT 50", like, like, like);
    }

    @Override
    public int countAll() {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT count(*) FROM poems");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new DaoException("Не удалось посчитать стихи", e);
        }
    }

    @Override
    public Optional<Long> authorOf(long poemId) {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT author_id FROM poems WHERE id = ?")) {
            ps.setLong(1, poemId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(rs.getLong(1)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Не удалось найти автора стиха", e);
        }
    }

    private List<Poem> query(String sql, Object... params) {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            List<Poem> list = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
            loadTags(c, list);
            return list;
        } catch (SQLException e) {
            throw new DaoException("Не удалось получить стихи", e);
        }
    }

    private Poem map(ResultSet rs) throws SQLException {
        Poem p = new Poem();
        p.setId(rs.getLong("id"));
        p.setAuthorId(rs.getLong("author_id"));
        p.setTitle(rs.getString("title"));
        p.setBody(rs.getString("body"));
        p.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        Timestamp updated = rs.getTimestamp("updated_at");
        p.setUpdatedAt(updated == null ? null : updated.toLocalDateTime());
        p.setAuthorName(rs.getString("author_name"));
        p.setLikeCount(rs.getInt("like_count"));
        double avg = rs.getDouble("avg_score");
        p.setAvgScore(rs.wasNull() ? null : avg);
        return p;
    }

    private void loadTags(Connection c, List<Poem> poems) throws SQLException {
        if (poems.isEmpty()) return;
        Map<Long, Poem> byId = new HashMap<>();
        for (Poem p : poems) {
            byId.put(p.getId(), p);
        }
        Array ids = c.createArrayOf("bigint", byId.keySet().toArray());
        String sql = "SELECT pt.poem_id, t.id, t.name FROM poem_tags pt "
                + "JOIN tags t ON t.id = pt.tag_id "
                + "WHERE pt.poem_id = ANY(?) ORDER BY t.name";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setArray(1, ids);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    byId.get(rs.getLong(1)).getTags().add(new Tag(rs.getInt(2), rs.getString(3)));
                }
            }
        }
    }

    private void linkTags(Connection c, long poemId, List<Integer> tagIds) throws SQLException {
        if (tagIds == null || tagIds.isEmpty()) return;
        String sql = "INSERT INTO poem_tags (poem_id, tag_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (Integer tagId : tagIds) {
                ps.setLong(1, poemId);
                ps.setInt(2, tagId);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    @Override
    public List<Poem> findFavorites(long userId) {
        return query(SELECT_BASE
                + "JOIN favorites f ON f.poem_id = p.id WHERE f.user_id = ?"
                + ORDER_LATEST, userId);
    }
}