package com.ivorytower.dao.jdbc;

import com.ivorytower.dao.LikeDao;

import javax.sql.DataSource;

public class JdbcLikeDao extends JdbcSupport implements LikeDao {

    public JdbcLikeDao(DataSource ds) {
        super(ds);
    }

    @Override
    public boolean add(long userId, long poemId) {
        return update("INSERT INTO poem_likes (user_id, poem_id) VALUES (?, ?) ON CONFLICT DO NOTHING",
                userId, poemId) > 0;
    }

    @Override
    public boolean remove(long userId, long poemId) {
        return update("DELETE FROM poem_likes WHERE user_id = ? AND poem_id = ?", userId, poemId) > 0;
    }

    @Override
    public boolean exists(long userId, long poemId) {
        return has("SELECT 1 FROM poem_likes WHERE user_id = ? AND poem_id = ?", userId, poemId);
    }

    @Override
    public int countByPoem(long poemId) {
        return count("SELECT count(*) FROM poem_likes WHERE poem_id = ?", poemId);
    }
}