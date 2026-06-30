package com.tecnorepara.controller;

import com.tecnorepara.dao.ProductoDAO;
import com.tecnorepara.model.Producto;

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
import javafx.scene.control.TableRow;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;

public class ProductoController {

    @FXML private TextField txtCodigo;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtCategoria;
    @FXML private TextField txtPrecioCompra;
    @FXML private TextField txtPrecioVenta;
    @FXML private TextField txtStockActual;
    @FXML private TextField txtStockMinimo;
    @FXML private TextField txtStockMaximo;
    @FXML private TextField txtIdProveedor;
    @FXML private TextField txtBuscar;

    @FXML private TableView<Producto> tablaProductos;

    @FXML private TableColumn<Producto, Integer> colId;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colDescripcion;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, Double> colPrecioCompra;
    @FXML private TableColumn<Producto, Double> colPrecioVenta;
    @FXML private TableColumn<Producto, Integer> colStockActual;
    @FXML private TableColumn<Producto, Integer> colStockMinimo;
    @FXML private TableColumn<Producto, Integer> colStockMaximo;
    @FXML private TableColumn<Producto, Integer> colIdProveedor;

    private final ProductoDAO productoDAO = new ProductoDAO();
    private Producto productoSeleccionado;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecioCompra.setCellValueFactory(new PropertyValueFactory<>("precioCompra"));
        colPrecioVenta.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colStockActual.setCellValueFactory(new PropertyValueFactory<>("stockActual"));
        colStockMinimo.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));
        colStockMaximo.setCellValueFactory(new PropertyValueFactory<>("stockMaximo"));
        colIdProveedor.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));
        
        listarProductos();

        tablaProductos.getSelectionModel().selectedItemProperty().addListener(
                (obs, anterior, producto) -> {
                    if (producto != null) {
                        productoSeleccionado = producto;
                        cargarProductoEnFormulario(producto);
                    }
                }
        );

        tablaProductos.setRowFactory(tv -> new TableRow<Producto>() {
        @Override
            protected void updateItem(Producto producto, boolean empty) {
                super.updateItem(producto, empty);

                if (empty || producto == null) {
                    setStyle("");
                } else if (producto.getStockActual() <= producto.getStockMinimo()) {
                    setStyle("-fx-background-color:#FEE2E2;");
                } else {
                    setStyle("");
                }
            }
        });

        permitirSoloNumerosDecimales(txtPrecioCompra);
        permitirSoloNumerosDecimales(txtPrecioVenta);

        permitirSoloEnteros(txtStockActual);
        permitirSoloEnteros(txtStockMinimo);
        permitirSoloEnteros(txtStockMaximo);
        permitirSoloEnteros(txtIdProveedor);
    }


    @FXML
    public void guardar() {

        if (txtCodigo.getText().isBlank() || txtDescripcion.getText().isBlank()) {
            mostrarMensaje(
                    "Campos obligatorios",
                    "El código y la descripción son obligatorios.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        try {
            Producto producto = obtenerProductoDesdeFormulario();

            boolean guardado = productoDAO.insertar(producto);

            if (guardado) {
                mostrarMensaje(
                        "Producto guardado",
                        "El producto fue registrado correctamente.",
                        Alert.AlertType.INFORMATION
                );

                limpiar();
                listarProductos();

            } else {
                mostrarMensaje(
                        "Producto existente",
                        "Ya existe un producto con ese código.",
                        Alert.AlertType.WARNING
                );
            }

        } catch (NumberFormatException e) {
            mostrarMensaje(
                    "Datos inválidos",
                    "Verifique precios, stock e ID proveedor. Deben ser valores numéricos.",
                    Alert.AlertType.ERROR
            );
        }
    }

    @FXML
    public void modificar() {

        if (productoSeleccionado == null) {
            mostrarMensaje(
                    "Modificar producto",
                    "Seleccione un producto de la tabla.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        if (txtCodigo.getText().isBlank() || txtDescripcion.getText().isBlank()) {
            mostrarMensaje(
                    "Campos obligatorios",
                    "El código y la descripción son obligatorios.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        try {
            Producto producto = obtenerProductoDesdeFormulario();
            producto.setIdProducto(productoSeleccionado.getIdProducto());

            boolean actualizado = productoDAO.actualizar(producto);

            if (actualizado) {
                mostrarMensaje(
                        "Producto actualizado",
                        "El producto fue actualizado correctamente.",
                        Alert.AlertType.INFORMATION
                );

                limpiar();
                listarProductos();

            } else {
                mostrarMensaje(
                        "Error",
                        "No se pudo actualizar. Verifique si el código ya existe.",
                        Alert.AlertType.ERROR
                );
            }

        } catch (NumberFormatException e) {
            mostrarMensaje(
                    "Datos inválidos",
                    "Verifique precios, stock e ID proveedor. Deben ser valores numéricos.",
                    Alert.AlertType.ERROR
            );
        }
    }

    @FXML
    public void eliminar() {

        if (productoSeleccionado == null) {
            mostrarMensaje(
                    "Eliminar producto",
                    "Seleccione un producto de la tabla.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Seguro que desea eliminar este producto?");

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        boolean eliminado =
                productoDAO.eliminar(productoSeleccionado.getIdProducto());

        if (eliminado) {
            mostrarMensaje(
                    "Producto eliminado",
                    "El producto fue eliminado correctamente.",
                    Alert.AlertType.INFORMATION
            );

            limpiar();
            listarProductos();

        } else {
            mostrarMensaje(
                    "Error",
                    "No se pudo eliminar el producto.",
                    Alert.AlertType.ERROR
            );
        }
    }

    @FXML
    public void limpiar() {
        txtCodigo.clear();
        txtDescripcion.clear();
        txtCategoria.clear();
        txtPrecioCompra.clear();
        txtPrecioVenta.clear();
        txtStockActual.clear();
        txtStockMinimo.clear();
        txtStockMaximo.clear();
        txtIdProveedor.clear();

        tablaProductos.getSelectionModel().clearSelection();
        productoSeleccionado = null;
    }

    private Producto obtenerProductoDesdeFormulario() {

        Producto producto = new Producto();

        producto.setCodigo(txtCodigo.getText().trim().toUpperCase());
        producto.setDescripcion(txtDescripcion.getText().trim().toUpperCase());
        producto.setCategoria(txtCategoria.getText().trim().toUpperCase());

        producto.setPrecioCompra(Double.parseDouble(txtPrecioCompra.getText().trim()));
        producto.setPrecioVenta(Double.parseDouble(txtPrecioVenta.getText().trim()));

        producto.setStockActual(Integer.parseInt(txtStockActual.getText().trim()));
        producto.setStockMinimo(Integer.parseInt(txtStockMinimo.getText().trim()));
        producto.setStockMaximo(Integer.parseInt(txtStockMaximo.getText().trim()));

        producto.setIdProveedor(Integer.parseInt(txtIdProveedor.getText().trim()));

        return producto;
    }

    private void cargarProductoEnFormulario(Producto producto) {
        txtCodigo.setText(producto.getCodigo());
        txtDescripcion.setText(producto.getDescripcion());
        txtCategoria.setText(producto.getCategoria());
        txtPrecioCompra.setText(String.valueOf(producto.getPrecioCompra()));
        txtPrecioVenta.setText(String.valueOf(producto.getPrecioVenta()));
        txtStockActual.setText(String.valueOf(producto.getStockActual()));
        txtStockMinimo.setText(String.valueOf(producto.getStockMinimo()));
        txtStockMaximo.setText(String.valueOf(producto.getStockMaximo()));
        txtIdProveedor.setText(String.valueOf(producto.getIdProveedor()));
    }

    private void listarProductos() {

        ObservableList<Producto> lista =
                FXCollections.observableArrayList(
                        productoDAO.listar()
                );

        FilteredList<Producto> listaFiltrada =
                new FilteredList<>(lista, p -> true);

        txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> {

            listaFiltrada.setPredicate(producto -> {

                if (newValue == null || newValue.isBlank()) {
                    return true;
                }

                String filtro = newValue.toLowerCase();

                return contiene(producto.getCodigo(), filtro)
                        || contiene(producto.getDescripcion(), filtro)
                        || contiene(producto.getCategoria(), filtro)
                        || contiene(String.valueOf(producto.getStockActual()), filtro);
            });
        });

        SortedList<Producto> listaOrdenada =
                new SortedList<>(listaFiltrada);

        listaOrdenada.comparatorProperty().bind(
                tablaProductos.comparatorProperty()
        );

        tablaProductos.setItems(listaOrdenada);
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

    private void permitirSoloEnteros(TextField campo) {
        campo.textProperty().addListener((obs, valorAnterior, valorNuevo) -> {
            if (!valorNuevo.matches("\\d*")) {
                campo.setText(valorAnterior);
            }
        });
    }

    private void permitirSoloNumerosDecimales(TextField campo) {
        campo.textProperty().addListener((obs, valorAnterior, valorNuevo) -> {
            if (!valorNuevo.matches("\\d*(\\.\\d*)?")) {
                campo.setText(valorAnterior);
            }
        });
    }

    @FXML
    public void exportarExcel() {

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet hoja = workbook.createSheet("Productos");

            Row encabezado = hoja.createRow(0);
            encabezado.createCell(0).setCellValue("ID");
            encabezado.createCell(1).setCellValue("Código");
            encabezado.createCell(2).setCellValue("Descripción");
            encabezado.createCell(3).setCellValue("Categoría");
            encabezado.createCell(4).setCellValue("Precio Compra");
            encabezado.createCell(5).setCellValue("Precio Venta");
            encabezado.createCell(6).setCellValue("Stock Actual");
            encabezado.createCell(7).setCellValue("Stock Mínimo");
            encabezado.createCell(8).setCellValue("Stock Máximo");
            encabezado.createCell(9).setCellValue("ID Proveedor");

            int fila = 1;

            for (Producto producto : tablaProductos.getItems()) {
                Row row = hoja.createRow(fila++);

                row.createCell(0).setCellValue(producto.getIdProducto());
                row.createCell(1).setCellValue(producto.getCodigo());
                row.createCell(2).setCellValue(producto.getDescripcion());
                row.createCell(3).setCellValue(producto.getCategoria());
                row.createCell(4).setCellValue(producto.getPrecioCompra());
                row.createCell(5).setCellValue(producto.getPrecioVenta());
                row.createCell(6).setCellValue(producto.getStockActual());
                row.createCell(7).setCellValue(producto.getStockMinimo());
                row.createCell(8).setCellValue(producto.getStockMaximo());
                row.createCell(9).setCellValue(producto.getIdProveedor());
            }

            for (int i = 0; i <= 9; i++) {
                hoja.autoSizeColumn(i);
            }

            FileOutputStream archivo = new FileOutputStream("productos.xlsx");
            workbook.write(archivo);
            archivo.close();

            mostrarMensaje(
                    "Excel generado",
                    "Archivo productos.xlsx generado correctamente.",
                    Alert.AlertType.INFORMATION
            );

        } catch (Exception e) {
            mostrarMensaje(
                    "Error",
                    "No se pudo exportar productos a Excel.",
                    Alert.AlertType.ERROR
            );
        }
    }
}