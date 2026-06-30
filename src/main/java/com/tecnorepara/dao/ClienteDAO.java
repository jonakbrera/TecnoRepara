package com.tecnorepara.dao;

import com.tecnorepara.model.Cliente;
import com.tecnorepara.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ClienteDAO {

    public boolean insertar(Cliente cliente) {

        String sql = """
                INSERT INTO clientes
                (dni,nombre,telefono,email,direccion)
                VALUES (?,?,?,?,?)
                """;

        try (
                Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, cliente.getDni());
            ps.setString(2, cliente.getNombre());
            ps.setString(3, cliente.getTelefono());
            ps.setString(4, cliente.getEmail());
            ps.setString(5, cliente.getDireccion());

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {

            if ("23505".equals(e.getSQLState())) {
                System.out.println("⚠ Cliente ya registrado con ese DNI");
            } else {
                System.out.println("❌ Error SQL: " + e.getMessage());
            }

            return false;
        }
    }

    public ArrayList<Cliente> listar() {

        ArrayList<Cliente> lista = new ArrayList<>();

        String sql = """
                SELECT id_cliente, dni, nombre, telefono, email, direccion
                FROM clientes
                ORDER BY id_cliente DESC
                """;

        try (
                Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                Cliente cliente = new Cliente();

                cliente.setIdCliente(rs.getInt("id_cliente"));
                cliente.setDni(rs.getString("dni"));
                cliente.setNombre(rs.getString("nombre"));
                cliente.setTelefono(rs.getString("telefono"));
                cliente.setEmail(rs.getString("email"));
                cliente.setDireccion(rs.getString("direccion"));

                lista.add(cliente);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al listar clientes");
            System.out.println(e.getMessage());
        }

        return lista;
    }
    public boolean eliminar(int idCliente) {

        String sql = """
            DELETE FROM clientes
            WHERE id_cliente = ?
            """;

        try (
            Connection conn = ConexionBD.conectar();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

        ps.setInt(1, idCliente);
        ps.executeUpdate();

        return true;

        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar cliente");
            System.out.println(e.getMessage());
            return false;
        }
    }
    public boolean actualizar(Cliente cliente) {

        String sql = """
                UPDATE clientes
                SET dni = ?,
                    nombre = ?,
                    telefono = ?,
                    email = ?,
                    direccion = ?
                WHERE id_cliente = ?
                """;

        try (
                Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, cliente.getDni());
            ps.setString(2, cliente.getNombre());
            ps.setString(3, cliente.getTelefono());
            ps.setString(4, cliente.getEmail());
            ps.setString(5, cliente.getDireccion());
            ps.setInt(6, cliente.getIdCliente());

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {

            if ("23505".equals(e.getSQLState())) {
                System.out.println("⚠ Ya existe otro cliente con ese DNI");
            } else {
                System.out.println("❌ Error al actualizar cliente");
                System.out.println(e.getMessage());
            }

            return false;
        }
    }
}