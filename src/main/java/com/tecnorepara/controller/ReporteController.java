package com.tecnorepara.controller;

import com.tecnorepara.dao.ReporteDAO;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ReporteController {

    @FXML private Label lblClientes;
    @FXML private Label lblProductos;
    @FXML private Label lblOrdenes;
    @FXML private Label lblPresupuestos;
    @FXML private Label lblFacturas;
    @FXML private Label lblStockBajo;
    @FXML private Label lblTotalFacturado;

    private final ReporteDAO reporteDAO = new ReporteDAO();

    @FXML
    public void initialize() {
        actualizarReportes();
    }

    @FXML
    public void actualizarReportes() {
        lblClientes.setText(String.valueOf(reporteDAO.contarClientes()));
        lblProductos.setText(String.valueOf(reporteDAO.contarProductos()));
        lblOrdenes.setText(String.valueOf(reporteDAO.contarOrdenes()));
        lblPresupuestos.setText(String.valueOf(reporteDAO.contarPresupuestos()));
        lblFacturas.setText(String.valueOf(reporteDAO.contarFacturas()));
        lblStockBajo.setText(String.valueOf(reporteDAO.contarStockBajo()));

        lblTotalFacturado.setText(
                "Gs. " + String.format("%,.0f", reporteDAO.totalFacturado())
        );
    }
}