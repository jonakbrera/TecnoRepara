package com.tecnorepara.dao;

import com.tecnorepara.model.Orden;
import com.tecnorepara.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OrdenDAO {

    public boolean insertar(Orden orden) {

        String sql = """
                INSERT INTO ordenes_reparacion
                (id_cliente, dispositivo, marca, modelo,
                 problema_reportado, diagnostico, trabajo_realizado,
                 estado, fecha_ingreso)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                """;

        try (
                Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, orden.getIdCliente());
            ps.setString(2, orden.getDispositivo());
            ps.setString(3, orden.getMarca());
            ps.setString(4, orden.getModelo());
            ps.setString(5, orden.getProblemaReportado());
            ps.setString(6, orden.getDiagnostico());
            ps.setString(7, orden.getTrabajoRealizado());
            ps.setString(8, orden.getEstado());

            ps.executeUpdate();
            return true;

        }catch (SQLException e) {

            System.out.println("========== ERROR ORDEN ==========");
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Mensaje: " + e.getMessage());
            System.out.println("================================");

            return false;
        }
    }

    public ArrayList<Orden> listar() {

        ArrayList<Orden> lista = new ArrayList<>();

        String sql = """
                SELECT o.id_orden,
                    o.id_cliente,
                    c.nombre AS nombre_cliente,
                    o.dispositivo,
                    o.marca,
                    o.modelo,
                    o.problema_reportado,
                    o.diagnostico,
                    o.trabajo_realizado,
                    o.estado,
                    o.fecha_ingreso,
                    o.fecha_presupuesto,
                    o.fecha_reparacion,
                    o.fecha_entrega
                FROM ordenes_reparacion o
                INNER JOIN clientes c
                ON o.id_cliente = c.id_cliente
                ORDER BY o.id_orden DESC
                """;    

        try (
                Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                Orden orden = new Orden();

                orden.setIdOrden(rs.getInt("id_orden"));
                orden.setIdCliente(rs.getInt("id_cliente"));
                orden.setNombreCliente(rs.getString("nombre_cliente"));

                orden.setDispositivo(rs.getString("dispositivo"));
                orden.setMarca(rs.getString("marca"));
                orden.setModelo(rs.getString("modelo"));

                orden.setProblemaReportado(rs.getString("problema_reportado"));
                orden.setDiagnostico(rs.getString("diagnostico"));
                orden.setTrabajoRealizado(rs.getString("trabajo_realizado"));
                orden.setEstado(rs.getString("estado"));

                orden.setFechaIngreso(String.valueOf(rs.getTimestamp("fecha_ingreso")));
                orden.setFechaPresupuesto(String.valueOf(rs.getTimestamp("fecha_presupuesto")));
                orden.setFechaReparacion(String.valueOf(rs.getTimestamp("fecha_reparacion")));
                orden.setFechaEntrega(String.valueOf(rs.getTimestamp("fecha_entrega")));

                lista.add(orden);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar órdenes");
            System.out.println(e.getMessage());
        }

        return lista;
    }
    public boolean actualizar(Orden orden) {

        String sql = """
                UPDATE ordenes_reparacion
                SET id_cliente = ?,
                    dispositivo = ?,
                    marca = ?,
                    modelo = ?,
                    problema_reportado = ?,
                    diagnostico = ?,
                    trabajo_realizado = ?,
                    estado = ?
                WHERE id_orden = ?
                """;

        try (
                Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, orden.getIdCliente());
            ps.setString(2, orden.getDispositivo());
            ps.setString(3, orden.getMarca());
            ps.setString(4, orden.getModelo());
            ps.setString(5, orden.getProblemaReportado());
            ps.setString(6, orden.getDiagnostico());
            ps.setString(7, orden.getTrabajoRealizado());
            ps.setString(8, orden.getEstado());
            ps.setInt(9, orden.getIdOrden());

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al actualizar orden");
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int idOrden) {

        String sql = """
                DELETE FROM ordenes_reparacion
                WHERE id_orden = ?
                """;

        try (
                Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idOrden);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al eliminar orden");
            System.out.println(e.getMessage());
            return false;
        }
    }
    public ArrayList<Orden> listarPendientesPresupuesto() {

        ArrayList<Orden> lista = new ArrayList<>();

        String sql = """
            SELECT o.id_orden,
                o.id_cliente,
                c.nombre AS nombre_cliente,
                o.dispositivo,
                o.marca,
                o.modelo
            FROM ordenes_reparacion o
            INNER JOIN clientes c
                ON o.id_cliente = c.id_cliente
            WHERE o.estado = 'PENDIENTE'
            ORDER BY o.id_orden
            """;

        try (
                Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                Orden orden = new Orden();

                orden.setIdOrden(rs.getInt("id_orden"));
                orden.setIdCliente(rs.getInt("id_cliente"));
                orden.setNombreCliente(rs.getString("nombre_cliente"));
                orden.setDispositivo(rs.getString("dispositivo"));
                orden.setMarca(rs.getString("marca"));
                orden.setModelo(rs.getString("modelo"));

                lista.add(orden);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return lista;
    }


}
