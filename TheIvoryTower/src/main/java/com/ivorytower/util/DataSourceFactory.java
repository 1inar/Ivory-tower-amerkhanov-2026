package com.ivorytower.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DataSourceFactory {
    private DataSourceFactory() {}

    public static HikariDataSource create(AppConfig cfg) {
        HikariConfig hc = new HikariConfig();
        hc.setJdbcUrl(cfg.get("db.url"));
        hc.setDriverClassName("org.postgresql.Driver");
        hc.setUsername(cfg.get("db.user"));
        hc.setPassword(cfg.get("db.password"));
        hc.setMaximumPoolSize(cfg.getInt("db.pool.size"));
        return new HikariDataSource(hc);
    }
}