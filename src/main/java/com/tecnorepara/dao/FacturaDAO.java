package com.tecnorepara.dao;

import com.tecnorepara.model.Factura;
import com.tecnorepara.model.OrdenFacturacion;
import com.tecnorepara.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class FacturaDAO {

    public boolean insertar(Factura factura) {

        String sql = """
                INSERT INTO facturas
                (id_orden, id_cliente, metodo_pago, total, estado, fecha_factura)
                VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                """;

        try (
                Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, factura.getIdOrden());
            ps.setInt(2, factura.getIdCliente());
            ps.setString(3, factura.getMetodoPago());
            ps.setDouble(4, factura.getTotal());
            ps.setString(5, factura.getEstado());

            ps.executeUpdate();

            actualizarOrden(factura.getIdOrden(), "ENTREGADO");

            return true;

        } catch (SQLException e) {
            System.out.println("Error al insertar factura");
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int idFactura) {

        String sql = """
                DELETE FROM facturas
                WHERE id_factura = ?
                """;

        try (
                Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idFactura);
            ps.executeUpdate();

            return true;

        } catch (SQLException e) {
            System.out.println("Error al eliminar factura");
            System.out.println(e.getMessage());
            return false;
        }
    }

    private void actualizarOrden(int idOrden, String estado) {

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
            System.out.println("Error al actualizar orden desde factura");
            System.out.println(e.getMessage());
        }
    }

    public ArrayList<Factura> listar() {

        ArrayList<Factura> lista = new ArrayList<>();

        String sql = """
                SELECT
                    f.id_factura,
                    f.id_orden,
                    f.id_cliente,
                    c.nombre,
                    f.metodo_pago,
                    f.total,
                    f.estado,
                    f.fecha_factura
                FROM facturas f
                INNER JOIN clientes c
                    ON f.id_cliente = c.id_cliente
                ORDER BY f.id_factura DESC
                """;

        try (
                Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                Factura factura = new Factura();

                factura.setIdFactura(rs.getInt("id_factura"));
                factura.setIdOrden(rs.getInt("id_orden"));
                factura.setIdCliente(rs.getInt("id_cliente"));
                factura.setNombreCliente(rs.getString("nombre"));
                factura.setMetodoPago(rs.getString("metodo_pago"));
                factura.setTotal(rs.getDouble("total"));
                factura.setEstado(rs.getString("estado"));
                factura.setFechaFactura(String.valueOf(rs.getTimestamp("fecha_factura")));

                lista.add(factura);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar facturas");
            System.out.println(e.getMessage());
        }

        return lista;
    }

    public ArrayList<OrdenFacturacion> listarOrdenesParaFacturar() {

        ArrayList<OrdenFacturacion> lista = new ArrayList<>();

        String sql = """
                SELECT
                    o.id_orden,
                    c.id_cliente,
                    c.nombre AS nombre_cliente,
                    o.dispositivo,
                    p.total
                FROM ordenes_reparacion o
                INNER JOIN clientes c
                    ON o.id_cliente = c.id_cliente
                INNER JOIN presupuestos p
                    ON o.id_orden = p.id_orden
                WHERE o.estado = 'PRESUPUESTADO'
                ORDER BY o.id_orden DESC
                """;

        try (
                Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                OrdenFacturacion orden = new OrdenFacturacion();

                orden.setIdOrden(rs.getInt("id_orden"));
                orden.setIdCliente(rs.getInt("id_cliente"));
                orden.setNombreCliente(rs.getString("nombre_cliente"));
                orden.setDispositivo(rs.getString("dispositivo"));
                orden.setTotal(rs.getDouble("total"));

                lista.add(orden);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar órdenes para facturar");
            System.out.println(e.getMessage());
        }

        return lista;
    }
}