package com.tecnorepara.controller;

import com.tecnorepara.dao.ClienteDAO;
import com.tecnorepara.model.Cliente;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;

public class ClienteController {

    @FXML private TextField txtDni;
    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtBuscar;

    @FXML private TableView<Cliente> tablaClientes;

    @FXML private TableColumn<Cliente, Integer> colId;
    @FXML private TableColumn<Cliente, String> colDni;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colEmail;
    @FXML private TableColumn<Cliente, String> colDireccion;

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private Cliente clienteSeleccionado;

    @FXML
    public void initialize() {

        colId.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));

        listarClientes();

        tablaClientes.getSelectionModel().selectedItemProperty().addListener(
                (obs, anterior, cliente) -> {

                    if (cliente != null) {
                        clienteSeleccionado = cliente;

                        txtDni.setText(cliente.getDni());
                        txtNombre.setText(cliente.getNombre());
                        txtTelefono.setText(cliente.getTelefono());
                        txtEmail.setText(cliente.getEmail());
                        txtDireccion.setText(cliente.getDireccion());
                    }
                }
        );
    }

    @FXML
    public void guardar() {

        if (txtDni.getText().isBlank() || txtNombre.getText().isBlank()) {
            mostrarMensaje(
                    "Campos obligatorios",
                    "El DNI y el nombre son obligatorios.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        Cliente cliente = new Cliente();

        cliente.setDni(txtDni.getText().trim().toUpperCase());
        cliente.setNombre(txtNombre.getText().trim().toUpperCase());
        cliente.setTelefono(txtTelefono.getText().trim());
        cliente.setEmail(txtEmail.getText().trim());
        cliente.setDireccion(txtDireccion.getText().trim().toUpperCase());

        boolean guardado = clienteDAO.insertar(cliente);

        if (guardado) {
            mostrarMensaje(
                    "Cliente guardado",
                    "El cliente fue registrado correctamente.",
                    Alert.AlertType.INFORMATION
            );

            limpiar();
            listarClientes();

        } else {
            mostrarMensaje(
                    "Cliente existente",
                    "Ya existe un cliente registrado con ese DNI.",
                    Alert.AlertType.WARNING
            );
        }
    }

    @FXML
    public void modificar() {

        if (clienteSeleccionado == null) {
            mostrarMensaje(
                    "Modificar cliente",
                    "Seleccione un cliente de la tabla.",
                    Alert.AlertType.WARNING
            );
            return;
        }
        if (!txtEmail.getText().isBlank() &&
                !emailValido(txtEmail.getText())) {

            mostrarMensaje(
                    "Email inválido",
                    "Ingrese un correo electrónico válido.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        if (txtDni.getText().isBlank() || txtNombre.getText().isBlank()) {
            mostrarMensaje(
                    "Campos obligatorios",
                    "El DNI y el nombre son obligatorios.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        Cliente cliente = new Cliente();

        cliente.setIdCliente(clienteSeleccionado.getIdCliente());
        cliente.setDni(txtDni.getText().trim().toUpperCase());
        cliente.setNombre(txtNombre.getText().trim().toUpperCase());
        cliente.setTelefono(txtTelefono.getText().trim());
        cliente.setEmail(txtEmail.getText().trim());
        cliente.setDireccion(txtDireccion.getText().trim().toUpperCase());

        boolean actualizado = clienteDAO.actualizar(cliente);

        if (actualizado) {
            mostrarMensaje(
                    "Cliente actualizado",
                    "Los datos del cliente fueron actualizados correctamente.",
                    Alert.AlertType.INFORMATION
            );

            limpiar();
            listarClientes();

        } else {
            mostrarMensaje(
                    "Error",
                    "No se pudo actualizar. Verifique si el DNI ya existe.",
                    Alert.AlertType.ERROR
            );
        }
    }

    @FXML
    public void eliminar() {

        if (clienteSeleccionado == null) {
            mostrarMensaje(
                    "Eliminar cliente",
                    "Seleccione un cliente de la tabla.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Seguro que desea eliminar este cliente?");

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        boolean eliminado =
                clienteDAO.eliminar(clienteSeleccionado.getIdCliente());

        if (eliminado) {
            mostrarMensaje(
                    "Cliente eliminado",
                    "El cliente fue eliminado correctamente.",
                    Alert.AlertType.INFORMATION
            );

            limpiar();
            listarClientes();

        } else {
            mostrarMensaje(
                    "Error",
                    "No se pudo eliminar el cliente.",
                    Alert.AlertType.ERROR
            );
        }
    }

    @FXML
    public void limpiar() {
        txtDni.clear();
        txtNombre.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtDireccion.clear();

        tablaClientes.getSelectionModel().clearSelection();
        clienteSeleccionado = null;
    }

    private void listarClientes() {

        ObservableList<Cliente> lista =
                FXCollections.observableArrayList(
                        clienteDAO.listar()
                );

        FilteredList<Cliente> listaFiltrada =
                new FilteredList<>(lista, p -> true);

        txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> {

            listaFiltrada.setPredicate(cliente -> {

                if (newValue == null || newValue.isBlank()) {
                    return true;
                }

                String filtro = newValue.toLowerCase();

                return contiene(cliente.getDni(), filtro)
                        || contiene(cliente.getNombre(), filtro)
                        || contiene(cliente.getTelefono(), filtro)
                        || contiene(cliente.getEmail(), filtro)
                        || contiene(cliente.getDireccion(), filtro);
            });
        });

        SortedList<Cliente> listaOrdenada =
                new SortedList<>(listaFiltrada);

        listaOrdenada.comparatorProperty().bind(
                tablaClientes.comparatorProperty()
        );

        tablaClientes.setItems(listaOrdenada);
    }

    private boolean contiene(String texto, String filtro) {
        return texto != null && texto.toLowerCase().contains(filtro);
    }

    private void mostrarMensaje(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private boolean emailValido(String email) {

        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");

    }

    @FXML
    public void exportarExcel() {

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet hoja = workbook.createSheet("Clientes");

            Row encabezado = hoja.createRow(0);
            encabezado.createCell(0).setCellValue("ID");
            encabezado.createCell(1).setCellValue("DNI");
            encabezado.createCell(2).setCellValue("Nombre");
            encabezado.createCell(3).setCellValue("Teléfono");
            encabezado.createCell(4).setCellValue("Email");
            encabezado.createCell(5).setCellValue("Dirección");

            int fila = 1;

            for (Cliente cliente : tablaClientes.getItems()) {
                Row row = hoja.createRow(fila++);

                row.createCell(0).setCellValue(cliente.getIdCliente());
                row.createCell(1).setCellValue(cliente.getDni());
                row.createCell(2).setCellValue(cliente.getNombre());
                row.createCell(3).setCellValue(cliente.getTelefono());
                row.createCell(4).setCellValue(cliente.getEmail());
                row.createCell(5).setCellValue(cliente.getDireccion());
            }

            for (int i = 0; i <= 5; i++) {
                hoja.autoSizeColumn(i);
            }

            FileOutputStream archivo = new FileOutputStream("clientes.xlsx");
            workbook.write(archivo);
            archivo.close();

            mostrarMensaje(
                    "Excel generado",
                    "Archivo clientes.xlsx generado correctamente.",
                    Alert.AlertType.INFORMATION
            );

        } catch (Exception e) {
            mostrarMensaje(
                    "Error",
                    "No se pudo exportar clientes a Excel.",
                    Alert.AlertType.ERROR
            );
        }
    }
}