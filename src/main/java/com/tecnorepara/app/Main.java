package com.tecnorepara.app;

import com.tecnorepara.dao.ClienteDAO;
import com.tecnorepara.model.Cliente;

public class Main {

    public static void main(String[] args) {

        Cliente cliente = new Cliente();

        cliente.setDni("5555555");
        cliente.setNombre("Jonathan Cabrera");
        cliente.setTelefono("0981123456");
        cliente.setEmail("jonathan@gmail.com");
        cliente.setDireccion("Asuncion");

        ClienteDAO dao = new ClienteDAO();

        if (dao.insertar(cliente)) {

            System.out.println("Cliente insertado correctamente");

        } else {

            System.out.println("Error al insertar cliente");
        }
    }
}