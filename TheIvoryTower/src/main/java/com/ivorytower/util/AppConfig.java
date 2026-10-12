package com.ivorytower.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class AppConfig {
    private final Properties props = new Properties();

    public AppConfig(InputStream in) {
        if (in == null) throw new IllegalStateException("config.properties не найден");
        try {
            props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось прочитать конфиг", e);
        }
    }
    public String get(String key)     { return props.getProperty(key); }
    public int getInt(String key)     { return Integer.parseInt(get(key)); }
    public boolean getBoolean(String key) { return Boolean.parseBoolean(get(key)); }
}