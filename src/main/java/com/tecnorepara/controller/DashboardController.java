package com.tecnorepara.controller;

import com.tecnorepara.dao.DashboardDAO;
import com.tecnorepara.util.SesionUsuario;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.Desktop;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DashboardController {

    @FXML private BorderPane panelPrincipal;

    @FXML private Label lblHora;
    @FXML private Label lblUltimoAcceso;
    @FXML private Label lblUsuario;

    @FXML private Label lblClientes;
    @FXML private Label lblProductos;
    @FXML private Label lblPendientes;
    @FXML private Label lblPresupuestadas;
    @FXML private Label lblEntregadas;
    @FXML private Label lblStockBajo;
    @FXML private Label lblFacturado;

    @FXML private PieChart graficoOrdenes;

    @FXML private Button btnInicio;
    @FXML private Button btnClientes;
    @FXML private Button btnProductos;
    @FXML private Button btnProveedores;
    @FXML private Button btnOrdenes;
    @FXML private Button btnPresupuestos;
    @FXML private Button btnFacturacion;
    @FXML private Button btnReportes;

    private final DashboardDAO dashboardDAO = new DashboardDAO();

    @FXML
    public void initialize() {
        cargarUsuario();
        cargarEstadisticas();
        cargarGrafico();
        iniciarReloj();
        iniciarActualizacionAutomatica();
        aplicarPermisos();
        marcarBotonActivo(btnInicio);

        lblUltimoAcceso.setText(
                "Último acceso: " +
                        LocalDateTime.now().format(
                                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                        )
        );
    }

    private void cargarUsuario() {
        if (SesionUsuario.usuarioActual != null) {
            lblUsuario.setText(
                    SesionUsuario.usuarioActual.getNombreUsuario()
                            + " ("
                            + SesionUsuario.usuarioActual.getRol()
                            + ")"
            );
        } else {
            lblUsuario.setText("Usuario");
        }
    }

    private void aplicarPermisos() {

        if (SesionUsuario.usuarioActual == null) {
            return;
        }

        String rol = SesionUsuario.usuarioActual.getRol().toUpperCase();

        switch (rol) {

            case "ADMINISTRADOR":
                break;

            case "TÉCNICO":
            case "TECNICO":
                ocultarBoton(btnProductos);
                ocultarBoton(btnProveedores);
                ocultarBoton(btnFacturacion);
                ocultarBoton(btnReportes);
                break;

            case "VENDEDOR":
                ocultarBoton(btnProductos);
                ocultarBoton(btnProveedores);
                ocultarBoton(btnPresupuestos);
                break;

            default:
                break;
        }
    }

    private void ocultarBoton(Button boton) {
        boton.setVisible(false);
        boton.setManaged(false);
    }

    private void cargarEstadisticas() {
        lblClientes.setText(String.valueOf(dashboardDAO.contarClientes()));
        lblProductos.setText(String.valueOf(dashboardDAO.contarProductos()));
        lblPendientes.setText(String.valueOf(dashboardDAO.contarOrdenesPendientes()));
        lblPresupuestadas.setText(String.valueOf(dashboardDAO.contarOrdenesPresupuestadas()));
        lblEntregadas.setText(String.valueOf(dashboardDAO.contarOrdenesEntregadas()));
        lblStockBajo.setText(String.valueOf(dashboardDAO.contarStockBajo()));
        lblFacturado.setText("Gs. " + String.format("%,.0f", dashboardDAO.totalFacturado()));
    }

    private void cargarGrafico() {
        graficoOrdenes.getData().clear();

        graficoOrdenes.getData().add(
                new PieChart.Data("Pendientes", dashboardDAO.contarOrdenesPendientes())
        );

        graficoOrdenes.getData().add(
                new PieChart.Data("Presupuestadas", dashboardDAO.contarOrdenesPresupuestadas())
        );

        graficoOrdenes.getData().add(
                new PieChart.Data("Entregadas", dashboardDAO.contarOrdenesEntregadas())
        );
    }

    private void iniciarReloj() {
        Timeline reloj = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    lblHora.setText(
                            LocalDateTime.now().format(
                                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                            )
                    );
                })
        );

        reloj.setCycleCount(Animation.INDEFINITE);
        reloj.play();
    }

    private void iniciarActualizacionAutomatica() {
        Timeline actualizar = new Timeline(
                new KeyFrame(Duration.seconds(10), e -> actualizarDashboard())
        );

        actualizar.setCycleCount(Animation.INDEFINITE);
        actualizar.play();
    }

    @FXML
    private void actualizarDashboard() {
        cargarEstadisticas();
        cargarGrafico();
    }

    @FXML
    private void abrirInicio() {
        try {
            Parent dashboard = FXMLLoader.load(
                    getClass().getResource("/fxml/Dashboard.fxml")
            );

            panelPrincipal.getScene().setRoot(dashboard);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirClientes() {
        marcarBotonActivo(btnClientes);
        cargarVista("/fxml/Clientes.fxml");
    }

    @FXML
    private void abrirProductos() {
        marcarBotonActivo(btnProductos);
        cargarVista("/fxml/Productos.fxml");
    }

    @FXML
    private void abrirProveedores() {
        marcarBotonActivo(btnProveedores);
        cargarVista("/fxml/Proveedores.fxml");
    }

    @FXML
    private void abrirOrdenes() {
        marcarBotonActivo(btnOrdenes);
        cargarVista("/fxml/Ordenes.fxml");
    }

    @FXML
    private void abrirPresupuestos() {
        marcarBotonActivo(btnPresupuestos);
        cargarVista("/fxml/Presupuestos.fxml");
    }

    @FXML
    private void abrirFacturacion() {
        marcarBotonActivo(btnFacturacion);
        cargarVista("/fxml/Facturacion.fxml");
    }

    @FXML
    private void abrirReportes() {
        marcarBotonActivo(btnReportes);
        cargarVista("/fxml/Reportes.fxml");
    }

    private void cargarVista(String ruta) {
        try {
            Parent vista = FXMLLoader.load(
                    getClass().getResource(ruta)
            );

            panelPrincipal.setCenter(vista);

            FadeTransition fade = new FadeTransition(
                    Duration.millis(200),
                    vista
            );

            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void marcarBotonActivo(Button botonActivo) {

        Button[] botones = {
                btnInicio,
                btnClientes,
                btnProductos,
                btnProveedores,
                btnOrdenes,
                btnPresupuestos,
                btnFacturacion,
                btnReportes
        };

        for (Button boton : botones) {
            if (boton != null) {
                boton.getStyleClass().remove("boton-activo");

                if (!boton.getStyleClass().contains("boton-menu")) {
                    boton.getStyleClass().add("boton-menu");
                }
            }
        }

        if (botonActivo != null &&
                !botonActivo.getStyleClass().contains("boton-activo")) {
            botonActivo.getStyleClass().add("boton-activo");
        }
    }

    @FXML
    private void cambiarTema() {
        mostrarMensaje(
                "Modo oscuro",
                "Modo oscuro pendiente para la versión final.",
                Alert.AlertType.INFORMATION
        );
    }

    @FXML
    private void backupBD() {

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "C:\\Program Files\\PostgreSQL\\17\\bin\\pg_dump.exe",
                    "-U",
                    "postgres",
                    "-F",
                    "c",
                    "-f",
                    "backup_tecnorepara.backup",
                    "tecnorepara"
            );

            pb.environment().put("PGPASSWORD", "1234");
            pb.start();

            mostrarMensaje(
                    "Backup",
                    "Backup generado correctamente.",
                    Alert.AlertType.INFORMATION
            );

        } catch (Exception e) {
            mostrarMensaje(
                    "Backup",
                    "No se pudo generar el backup.",
                    Alert.AlertType.ERROR
            );
        }
    }

    @FXML
    private void abrirCarpetaProyecto() {
        try {
            Desktop.getDesktop().open(new File("."));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void mostrarAcercaDe() {

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Acerca de TecnoRepara");
        alerta.setHeaderText("TecnoRepara - Sistema Integral de Gestión");
        alerta.setContentText("""
                Versión: 1.0

                Módulos:
                - Clientes
                - Productos
                - Proveedores
                - Órdenes
                - Presupuestos
                - Facturación
                - Reportes

                Proyecto académico desarrollado en JavaFX y PostgreSQL.
                """);

        alerta.showAndWait();
    }

    @FXML
    private void cerrarSesion() {

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Cerrar sesión");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Seguro que desea cerrar sesión?");

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try {
            SesionUsuario.usuarioActual = null;

            Parent login = FXMLLoader.load(
                    getClass().getResource("/fxml/Login.fxml")
            );

            Stage stage = (Stage) panelPrincipal.getScene().getWindow();

            stage.setMaximized(false);
            stage.setWidth(900);
            stage.setHeight(560);
            stage.centerOnScreen();
            stage.setTitle("TecnoRepara - Login");
            stage.setScene(new Scene(login));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarMensaje(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}