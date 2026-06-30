package com.tecnorepara.dao;

import com.tecnorepara.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReporteDAO {

    public int contarClientes() {
        return contar("SELECT COUNT(*) FROM clientes");
    }

    public int contarProductos() {
        return contar("SELECT COUNT(*) FROM productos");
    }

    public int contarOrdenes() {
        return contar("SELECT COUNT(*) FROM ordenes_reparacion");
    }

    public int contarPresupuestos() {
        return contar("SELECT COUNT(*) FROM presupuestos");
    }

    public int contarFacturas() {
        return contar("SELECT COUNT(*) FROM facturas");
    }

    public double totalFacturado() {
        String sql = "SELECT COALESCE(SUM(total), 0) FROM facturas";

        try (
                Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.out.println("Error al calcular total facturado");
            System.out.println(e.getMessage());
        }

        return 0;
    }

    public int contarStockBajo() {
        return contar("""
                SELECT COUNT(*)
                FROM productos
                WHERE stock_actual <= stock_minimo
                """);
    }

    private int contar(String sql) {
        try (
                Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error en ReporteDAO");
            System.out.println(e.getMessage());
        }

        return 0;
    }
}
