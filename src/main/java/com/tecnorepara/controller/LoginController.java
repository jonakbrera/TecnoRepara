package com.tecnorepara.controller;

import com.tecnorepara.dao.UsuarioDAO;
import com.tecnorepara.model.Usuario;
import com.tecnorepara.util.SesionUsuario;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;

    @FXML
    private void ingresar() {

        String usuario = txtUsuario.getText().trim();
        String password = txtPassword.getText().trim();

        if (usuario.isBlank() || password.isBlank()) {
            mostrarMensaje("Campos obligatorios", "Ingrese usuario y contraseña.", Alert.AlertType.WARNING);
            return;
        }

        UsuarioDAO dao = new UsuarioDAO();

        Usuario usuarioLogueado = dao.validarLogin(usuario, password);

        if (usuarioLogueado != null) {

            SesionUsuario.usuarioActual = usuarioLogueado;
            abrirDashboard();

        } else {
            mostrarMensaje("Acceso denegado", "Usuario o contraseña incorrectos.", Alert.AlertType.ERROR);
        }
    }

    private void abrirDashboard() {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/fxml/Dashboard.fxml")
            );

            Stage stage = (Stage) txtUsuario.getScene().getWindow();

            stage.setTitle("TecnoRepara - Dashboard");
            stage.setScene(new Scene(root));

            stage.setMaximized(true);
            stage.centerOnScreen();

            stage.show();

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