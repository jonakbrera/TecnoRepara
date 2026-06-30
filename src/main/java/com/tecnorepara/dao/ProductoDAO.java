package com.tecnorepara.dao;

import com.tecnorepara.model.Producto;
import com.tecnorepara.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProductoDAO {

    public boolean insertar(Producto producto) {

        String sql = """
                INSERT INTO productos
                (codigo, descripcion, categoria, precio_compra, precio_venta,
                 stock_actual, stock_minimo, stock_maximo, id_proveedor)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getDescripcion());
            ps.setString(3, producto.getCategoria());
            ps.setDouble(4, producto.getPrecioCompra());
            ps.setDouble(5, producto.getPrecioVenta());
            ps.setInt(6, producto.getStockActual());
            ps.setInt(7, producto.getStockMinimo());
            ps.setInt(8, producto.getStockMaximo());
            ps.setInt(9, producto.getIdProveedor());

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {

            if ("23505".equals(e.getSQLState())) {
                System.out.println("⚠ Ya existe un producto con ese código");
            } else {
                System.out.println("❌ Error al insertar producto");
                System.out.println(e.getMessage());
            }

            return false;
        }
    }

    public ArrayList<Producto> listar() {

        ArrayList<Producto> lista = new ArrayList<>();

        String sql = """
                SELECT id_producto, codigo, descripcion, categoria,
                       precio_compra, precio_venta,
                       stock_actual, stock_minimo, stock_maximo,
                       id_proveedor
                FROM productos
                ORDER BY id_producto DESC
                """;

        try (
                Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                Producto producto = new Producto();

                producto.setIdProducto(rs.getInt("id_producto"));
                producto.setCodigo(rs.getString("codigo"));
                producto.setDescripcion(rs.getString("descripcion"));
                producto.setCategoria(rs.getString("categoria"));
                producto.setPrecioCompra(rs.getDouble("precio_compra"));
                producto.setPrecioVenta(rs.getDouble("precio_venta"));
                producto.setStockActual(rs.getInt("stock_actual"));
                producto.setStockMinimo(rs.getInt("stock_minimo"));
                producto.setStockMaximo(rs.getInt("stock_maximo"));
                producto.setIdProveedor(rs.getInt("id_proveedor"));

                lista.add(producto);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al listar productos");
            System.out.println(e.getMessage());
        }

        return lista;
    }

    public boolean actualizar(Producto producto) {

        String sql = """
                UPDATE productos
                SET codigo = ?,
                    descripcion = ?,
                    categoria = ?,
                    precio_compra = ?,
                    precio_venta = ?,
                    stock_actual = ?,
                    stock_minimo = ?,
                    stock_maximo = ?,
                    id_proveedor = ?
                WHERE id_producto = ?
                """;

        try (
                Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getDescripcion());
            ps.setString(3, producto.getCategoria());
            ps.setDouble(4, producto.getPrecioCompra());
            ps.setDouble(5, producto.getPrecioVenta());
            ps.setInt(6, producto.getStockActual());
            ps.setInt(7, producto.getStockMinimo());
            ps.setInt(8, producto.getStockMaximo());
            ps.setInt(9, producto.getIdProveedor());
            ps.setInt(10, producto.getIdProducto());

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {

            if ("23505".equals(e.getSQLState())) {
                System.out.println("⚠ Ya existe otro producto con ese código");
            } else {
                System.out.println("❌ Error al actualizar producto");
                System.out.println(e.getMessage());
            }

            return false;
        }
    }

    public boolean eliminar(int idProducto) {

        String sql = """
                DELETE FROM productos
                WHERE id_producto = ?
                """;

        try (
                Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idProducto);
            ps.executeUpdate();

            return true;

        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar producto");
            System.out.println(e.getMessage());
            return false;
        }
    }
}
