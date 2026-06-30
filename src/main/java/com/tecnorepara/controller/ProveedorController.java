package com.tecnorepara.controller;

import com.tecnorepara.dao.ProveedorDAO;
import com.tecnorepara.model.Proveedor;

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

public class ProveedorController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtBuscar;

    @FXML private TableView<Proveedor> tablaProveedores;

    @FXML private TableColumn<Proveedor, Integer> colId;
    @FXML private TableColumn<Proveedor, String> colNombre;
    @FXML private TableColumn<Proveedor, String> colTelefono;
    @FXML private TableColumn<Proveedor, String> colEmail;
    @FXML private TableColumn<Proveedor, String> colDireccion;

    private final ProveedorDAO proveedorDAO = new ProveedorDAO();
    private Proveedor proveedorSeleccionado;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));

        listarProveedores();

        tablaProveedores.getSelectionModel().selectedItemProperty().addListener(
                (obs, anterior, proveedor) -> {
                    if (proveedor != null) {
                        proveedorSeleccionado = proveedor;
                        cargarProveedorEnFormulario(proveedor);
                    }
                }
        );
    }
    
    @FXML
    public void guardar() {

        if (txtNombre.getText().isBlank()) {
            mostrarMensaje(
                    "Campo obligatorio",
                    "El nombre del proveedor es obligatorio.",
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

        Proveedor proveedor = obtenerProveedorDesdeFormulario();

        boolean guardado = proveedorDAO.insertar(proveedor);

        if (guardado) {
            mostrarMensaje(
                    "Proveedor guardado",
                    "El proveedor fue registrado correctamente.",
                    Alert.AlertType.INFORMATION
            );

            limpiar();
            listarProveedores();

        } else {
            mostrarMensaje(
                    "Error",
                    "No se pudo guardar el proveedor.",
                    Alert.AlertType.ERROR
            );
        }
    }
    
    @FXML
    public void modificar() {

        if (proveedorSeleccionado == null) {
            mostrarMensaje(
                    "Modificar proveedor",
                    "Seleccione un proveedor de la tabla.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        if (txtNombre.getText().isBlank()) {
            mostrarMensaje(
                    "Campo obligatorio",
                    "El nombre del proveedor es obligatorio.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        Proveedor proveedor = obtenerProveedorDesdeFormulario();
        proveedor.setIdProveedor(proveedorSeleccionado.getIdProveedor());

        boolean actualizado = proveedorDAO.actualizar(proveedor);

        if (actualizado) {
            mostrarMensaje(
                    "Proveedor actualizado",
                    "Los datos del proveedor fueron actualizados correctamente.",
                    Alert.AlertType.INFORMATION
            );

            limpiar();
            listarProveedores();

        } else {
            mostrarMensaje(
                    "Error",
                    "No se pudo actualizar el proveedor.",
                    Alert.AlertType.ERROR
            );
        }
    }

    @FXML
    public void eliminar() {

        if (proveedorSeleccionado == null) {
            mostrarMensaje(
                    "Eliminar proveedor",
                    "Seleccione un proveedor de la tabla.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Seguro que desea eliminar este proveedor?");

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        boolean eliminado =
                proveedorDAO.eliminar(proveedorSeleccionado.getIdProveedor());

        if (eliminado) {
            mostrarMensaje(
                    "Proveedor eliminado",
                    "El proveedor fue eliminado correctamente.",
                    Alert.AlertType.INFORMATION
            );

            limpiar();
            listarProveedores();

        } else {
            mostrarMensaje(
                    "Error",
                    "No se pudo eliminar el proveedor. Verifique si tiene productos asociados.",
                    Alert.AlertType.ERROR
            );
        }
    }

    @FXML
    public void limpiar() {
        txtNombre.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtDireccion.clear();

        tablaProveedores.getSelectionModel().clearSelection();
        proveedorSeleccionado = null;
    }

    private Proveedor obtenerProveedorDesdeFormulario() {
        Proveedor proveedor = new Proveedor();

        proveedor.setNombre(txtNombre.getText().trim().toUpperCase());
        proveedor.setTelefono(txtTelefono.getText().trim());
        proveedor.setEmail(txtEmail.getText().trim());
        proveedor.setDireccion(txtDireccion.getText().trim().toUpperCase());

        return proveedor;
    }

    private void cargarProveedorEnFormulario(Proveedor proveedor) {
        txtNombre.setText(proveedor.getNombre());
        txtTelefono.setText(proveedor.getTelefono());
        txtEmail.setText(proveedor.getEmail());
        txtDireccion.setText(proveedor.getDireccion());
    }

    private void listarProveedores() {

        ObservableList<Proveedor> lista =
                FXCollections.observableArrayList(
                        proveedorDAO.listar()
                );

        FilteredList<Proveedor> listaFiltrada =
                new FilteredList<>(lista, p -> true);

        txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> {

            listaFiltrada.setPredicate(proveedor -> {

                if (newValue == null || newValue.isBlank()) {
                    return true;
                }

                String filtro = newValue.toLowerCase();

                return contiene(proveedor.getNombre(), filtro)
                        || contiene(proveedor.getTelefono(), filtro)
                        || contiene(proveedor.getEmail(), filtro)
                        || contiene(proveedor.getDireccion(), filtro);
            });
        });

        SortedList<Proveedor> listaOrdenada =
                new SortedList<>(listaFiltrada);

        listaOrdenada.comparatorProperty().bind(
                tablaProveedores.comparatorProperty()
        );

        tablaProveedores.setItems(listaOrdenada);
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

            Sheet hoja = workbook.createSheet("Proveedores");

            Row encabezado = hoja.createRow(0);
            encabezado.createCell(0).setCellValue("ID");
            encabezado.createCell(1).setCellValue("Nombre");
            encabezado.createCell(2).setCellValue("Teléfono");
            encabezado.createCell(3).setCellValue("Email");
            encabezado.createCell(4).setCellValue("Dirección");

            int fila = 1;

            for (Proveedor proveedor : tablaProveedores.getItems()) {
                Row row = hoja.createRow(fila++);

                row.createCell(0).setCellValue(proveedor.getIdProveedor());
                row.createCell(1).setCellValue(proveedor.getNombre());
                row.createCell(2).setCellValue(proveedor.getTelefono());
                row.createCell(3).setCellValue(proveedor.getEmail());
                row.createCell(4).setCellValue(proveedor.getDireccion());
            }

            for (int i = 0; i <= 4; i++) {
                hoja.autoSizeColumn(i);
            }

            FileOutputStream archivo = new FileOutputStream("proveedores.xlsx");
            workbook.write(archivo);
            archivo.close();

            mostrarMensaje("Excel generado", "Archivo proveedores.xlsx generado correctamente.", Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            mostrarMensaje("Error", "No se pudo exportar proveedores a Excel.", Alert.AlertType.ERROR);
        }
    }
}