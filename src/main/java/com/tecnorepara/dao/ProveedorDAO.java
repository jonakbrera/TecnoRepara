package com.tecnorepara.dao;

import com.tecnorepara.model.Proveedor;
import com.tecnorepara.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProveedorDAO {

    public boolean insertar(Proveedor proveedor) {

        String sql = """
                INSERT INTO proveedores
                (nombre, telefono, email, direccion)
                VALUES (?, ?, ?, ?)
                """;

        try (
                Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, proveedor.getNombre());
            ps.setString(2, proveedor.getTelefono());
            ps.setString(3, proveedor.getEmail());
            ps.setString(4, proveedor.getDireccion());

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al insertar proveedor");
            System.out.println(e.getMessage());
            return false;
        }
    }

    public ArrayList<Proveedor> listar() {

        ArrayList<Proveedor> lista = new ArrayList<>();

        String sql = """
                SELECT id_proveedor, nombre, telefono, email, direccion
                FROM proveedores
                ORDER BY id_proveedor DESC
                """;

        try (
                Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                Proveedor proveedor = new Proveedor();

                proveedor.setIdProveedor(rs.getInt("id_proveedor"));
                proveedor.setNombre(rs.getString("nombre"));
                proveedor.setTelefono(rs.getString("telefono"));
                proveedor.setEmail(rs.getString("email"));
                proveedor.setDireccion(rs.getString("direccion"));

                lista.add(proveedor);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar proveedores");
            System.out.println(e.getMessage());
        }

        return lista;
    }

    public boolean actualizar(Proveedor proveedor) {

        String sql = """
                UPDATE proveedores
                SET nombre = ?,
                    telefono = ?,
                    email = ?,
                    direccion = ?
                WHERE id_proveedor = ?
                """;

        try (
                Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, proveedor.getNombre());
            ps.setString(2, proveedor.getTelefono());
            ps.setString(3, proveedor.getEmail());
            ps.setString(4, proveedor.getDireccion());
            ps.setInt(5, proveedor.getIdProveedor());

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al actualizar proveedor");
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int idProveedor) {

        String sql = """
                DELETE FROM proveedores
                WHERE id_proveedor = ?
                """;

        try (
                Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idProveedor);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al eliminar proveedor");
            System.out.println(e.getMessage());
            return false;
        }
    }
}