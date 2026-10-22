package com.ivorytower.dao.jdbc;

import com.ivorytower.dao.FavoriteDao;

import javax.sql.DataSource;

public class JdbcFavoriteDao extends JdbcSupport implements FavoriteDao {

    public JdbcFavoriteDao(DataSource ds) {
        super(ds);
    }

    @Override
    public boolean add(long userId, long poemId) {
        return update("INSERT INTO favorites (user_id, poem_id) VALUES (?, ?) ON CONFLICT DO NOTHING",
                userId, poemId) > 0;
    }

    @Override
    public boolean remove(long userId, long poemId) {
        return update("DELETE FROM favorites WHERE user_id = ? AND poem_id = ?", userId, poemId) > 0;
    }

    @Override
    public boolean exists(long userId, long poemId) {
        return has("SELECT 1 FROM favorites WHERE user_id = ? AND poem_id = ?", userId, poemId);
    }
}