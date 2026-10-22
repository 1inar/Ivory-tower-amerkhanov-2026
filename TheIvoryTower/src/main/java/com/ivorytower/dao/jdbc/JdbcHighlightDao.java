package com.ivorytower.dao.jdbc;

import com.ivorytower.dao.HighlightDao;
import com.ivorytower.exception.DaoException;
import com.ivorytower.model.LineStat;
import com.ivorytower.model.Poem;
import com.ivorytower.model.Quote;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcHighlightDao extends JdbcSupport implements HighlightDao {

    public JdbcHighlightDao(DataSource ds) {
        super(ds);
    }

    @Override
    public boolean add(long userId, long poemId, int lineNo) {
        return update("INSERT INTO highlights (user_id, poem_id, line_no) VALUES (?, ?, ?) "
                + "ON CONFLICT DO NOTHING", userId, poemId, lineNo) > 0;
    }

    @Override
    public boolean remove(long userId, long poemId, int lineNo) {
        return update("DELETE FROM highlights WHERE user_id = ? AND poem_id = ? AND line_no = ?",
                userId, poemId, lineNo) > 0;
    }

    @Override
    public int countByUserAndPoem(long userId, long poemId) {
        return count("SELECT count(*) FROM highlights WHERE user_id = ? AND poem_id = ?", userId, poemId);
    }

    @Override
    public List<LineStat> statsByPoem(long poemId, Long currentUserId) {
        long uid = currentUserId == null ? -1 : currentUserId;
        String sql = "SELECT line_no, count(*) AS cnt, bool_or(user_id = ?) AS mine "
                + "FROM highlights WHERE poem_id = ? GROUP BY line_no ORDER BY line_no";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, uid);
            ps.setLong(2, poemId);
            try (ResultSet rs = ps.executeQuery()) {
                List<LineStat> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new LineStat(rs.getInt("line_no"), rs.getInt("cnt"), rs.getBoolean("mine")));
                }
                return list;
            }
        } catch (SQLException e) {
            throw new DaoException("Не удалось получить подчёркивания", e);
        }
    }

    @Override
    public List<Quote> topQuotes(int limit) {
        String sql = "SELECT h.poem_id, h.line_no, count(*) AS cnt, "
                + "       p.title, p.body, p.author_id, u.username "
                + "FROM highlights h "
                + "JOIN poems p ON p.id = h.poem_id "
                + "JOIN users u ON u.id = p.author_id "
                + "GROUP BY h.poem_id, h.line_no, p.id, u.id "
                + "ORDER BY cnt DESC, h.poem_id DESC, h.line_no ASC "
                + "LIMIT ?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                List<Quote> list = new ArrayList<>();
                while (rs.next()) {
                    Poem tmp = new Poem();
                    tmp.setBody(rs.getString("body"));
                    List<String> lines = tmp.getLines();
                    int lineNo = rs.getInt("line_no");
                    if (lineNo < 1 || lineNo > lines.size() || lines.get(lineNo - 1).isBlank()) {
                        continue;
                    }
                    Quote q = new Quote();
                    q.setPoemId(rs.getLong("poem_id"));
                    q.setPoemTitle(rs.getString("title"));
                    q.setAuthorId(rs.getLong("author_id"));
                    q.setAuthorName(rs.getString("username"));
                    q.setLineNo(lineNo);
                    q.setLineText(lines.get(lineNo - 1));
                    q.setCount(rs.getInt("cnt"));
                    list.add(q);
                }
                return list;
            }
        } catch (SQLException e) {
            throw new DaoException("Не удалось получить цитатник", e);
        }
    }
}