package com.tecnorepara.dao;

import com.tecnorepara.model.Presupuesto;
import com.tecnorepara.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;

public class PresupuestoDAO {

    public boolean insertar(Presupuesto presupuesto) {

        String sql = """
                INSERT INTO presupuestos
                (id_orden, mano_obra, total_repuestos, total, estado, fecha_presupuesto)
                VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                """;

        try (
                Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, presupuesto.getIdOrden());
            ps.setDouble(2, presupuesto.getManoObra());
            ps.setDouble(3, presupuesto.getTotalRepuestos());
            ps.setDouble(4, presupuesto.getTotal());
            ps.setString(5, presupuesto.getEstado());

            ps.executeUpdate();
            actualizarEstadoOrden(
                    presupuesto.getIdOrden(),
                    "PRESUPUESTADO");
            return true;

        } catch (SQLException e) {
            System.out.println("Error al insertar presupuesto");
            System.out.println(e.getMessage());
            return false;
        }
    }

    public ArrayList<Presupuesto> listar() {

        ArrayList<Presupuesto> lista = new ArrayList<>();

        String sql = """
               SELECT
                    p.id_presupuesto,
                    p.id_orden,
                    CONCAT(
                        o.id_orden,
                        ' - ',
                        o.dispositivo,
                        ' ',
                        o.marca,
                        ' ',
                        o.modelo,
                        ' - ',
                        c.nombre
                    ) AS descripcion_orden,
                    p.mano_obra,
                    p.total_repuestos,
                    p.total,
                    p.estado,
                    p.fecha_presupuesto
                FROM presupuestos p
                INNER JOIN ordenes_reparacion o
                    ON p.id_orden = o.id_orden
                INNER JOIN clientes c
                    ON o.id_cliente = c.id_cliente
                ORDER BY p.id_presupuesto DESC;
                """;

        try (
                Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                Presupuesto presupuesto = new Presupuesto();

                presupuesto.setIdPresupuesto(rs.getInt("id_presupuesto"));
                presupuesto.setIdOrden(rs.getInt("id_orden"));
                presupuesto.setManoObra(rs.getDouble("mano_obra"));
                presupuesto.setTotalRepuestos(rs.getDouble("total_repuestos"));
                presupuesto.setTotal(rs.getDouble("total"));
                presupuesto.setEstado(rs.getString("estado"));
                presupuesto.setFechaPresupuesto(String.valueOf(rs.getTimestamp("fecha_presupuesto")));
                presupuesto.setDescripcionOrden(rs.getString("descripcion_orden"));
                lista.add(presupuesto);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar presupuestos");
            System.out.println(e.getMessage());
        }

        return lista;
    }

    public boolean actualizar(Presupuesto presupuesto) {

        String sql = """
                UPDATE presupuestos
                SET id_orden = ?,
                    mano_obra = ?,
                    total_repuestos = ?,
                    total = ?,
                    estado = ?
                WHERE id_presupuesto = ?
                """;

        try (
                Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, presupuesto.getIdOrden());
            ps.setDouble(2, presupuesto.getManoObra());
            ps.setDouble(3, presupuesto.getTotalRepuestos());
            ps.setDouble(4, presupuesto.getTotal());
            ps.setString(5, presupuesto.getEstado());
            ps.setInt(6, presupuesto.getIdPresupuesto());

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al actualizar presupuesto");
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int idPresupuesto) {

        String sql = """
                DELETE FROM presupuestos
                WHERE id_presupuesto = ?
                """;

        try (
                Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idPresupuesto);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al eliminar presupuesto");
            System.out.println(e.getMessage());
            return false;
        }
    }

    private void actualizarEstadoOrden(int idOrden, String estado) {

        String sql = """
                UPDATE ordenes_reparacion
                SET estado = ?
                WHERE id_orden = ?
                """;

        try (
                Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, estado);
            ps.setInt(2, idOrden);

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error al actualizar estado de orden");
            System.out.println(e.getMessage());
        }
    }
}