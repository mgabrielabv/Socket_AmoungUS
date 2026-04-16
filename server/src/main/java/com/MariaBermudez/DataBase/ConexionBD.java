package com.MariaBermudez.DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    private static Connection conexion = null;

    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }

    private static String buildJdbcUrl() {
        String url = System.getenv("DB_URL");
        if (url != null && !url.isBlank()) {
            return url;
        }

        String host = getEnvOrDefault("DB_HOST", "localhost");
        String port = getEnvOrDefault("DB_PORT", "5433");
        String name = getEnvOrDefault("DB_NAME", "amongus");
        return "jdbc:postgresql://" + host + ":" + port + "/" + name;
    }

    public static Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                String url = buildJdbcUrl();
                String user = getEnvOrDefault("DB_USER", "admin");
                String pass = getEnvOrDefault("DB_PASSWORD", "admin123");
                conexion = DriverManager.getConnection(url, user, pass);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conexion;
    }
}