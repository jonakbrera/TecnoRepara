package com.tecnorepara.dao;

import com.tecnorepara.model.Usuario;
import com.tecnorepara.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UsuarioDAO {

    public Usuario validarLogin(String usuario, String contrasena) {

        String sql = """
            SELECT *
            FROM usuarios
            WHERE nombre_usuario=?
            AND contrasena=?
            AND estado=true
            """;

        try(
                Connection cn=ConexionBD.conectar();
                PreparedStatement ps=cn.prepareStatement(sql)
        ){

            ps.setString(1,usuario);
            ps.setString(2,contrasena);

            ResultSet rs=ps.executeQuery();

            if(rs.next()){

                Usuario u=new Usuario();

                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setNombreUsuario(rs.getString("nombre_usuario"));
                u.setRol(rs.getString("rol"));
                u.setEstado(rs.getBoolean("estado"));

                return u;

            }

        }catch(Exception e){

            e.printStackTrace();

        }

        return null;

    }

}