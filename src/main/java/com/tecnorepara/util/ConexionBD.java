package com.tecnorepara.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

  private static final String URL =
    "jdbc:postgresql://localhost:8080/tecnorepara_db";

    private static final String USUARIO =
            "postgres";

    private static final String PASSWORD =
            "postgres";

    public static Connection conectar() {

        try {

            Connection conexion =
                    DriverManager.getConnection(
                            URL,
                            USUARIO,
                            PASSWORD
                    );

            System.out.println("✅ Conexión exitosa");

            return conexion;

        } catch (SQLException e) {

            System.out.println("❌ Error de conexión");
            System.out.println(e.getMessage());

            return null;
        }
    }
}