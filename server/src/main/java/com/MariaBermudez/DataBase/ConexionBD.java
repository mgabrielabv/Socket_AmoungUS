package com.MariaBermudez.DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    private static Connection conexion = null;
    private static final String URL = "jdbc:postgresql";
    private static final String USER = "postgres";
    private static final String PASS = "password";

    public static Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = DriverManager.getConnection(URL, USER, PASS);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conexion;
    }
}