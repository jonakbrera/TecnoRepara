package com.tecnorepara.dao;

import com.tecnorepara.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardDAO {

    public int contarClientes() {
        return contar("SELECT COUNT(*) FROM clientes");
    }

    public int contarProductos() {
        return contar("SELECT COUNT(*) FROM productos");
    }

    public int contarOrdenesPendientes() {
        return contar("SELECT COUNT(*) FROM ordenes_reparacion WHERE estado = 'PENDIENTE'");
    }

    public int contarOrdenesPresupuestadas() {
        return contar("SELECT COUNT(*) FROM ordenes_reparacion WHERE estado = 'PRESUPUESTADO'");
    }

    public int contarOrdenesEntregadas() {
        return contar("SELECT COUNT(*) FROM ordenes_reparacion WHERE estado = 'ENTREGADO'");
    }

    public int contarStockBajo() {
        return contar("SELECT COUNT(*) FROM productos WHERE stock_actual <= stock_minimo");
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
            System.out.println("Error en DashboardDAO");
            System.out.println(e.getMessage());
        }

        return 0;
    }
}