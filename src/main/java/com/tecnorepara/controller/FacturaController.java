package com.tecnorepara.controller;

import com.tecnorepara.dao.FacturaDAO;
import com.tecnorepara.model.Factura;
import com.tecnorepara.model.OrdenFacturacion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;



public class FacturaController {

    @FXML private ComboBox<OrdenFacturacion> cmbOrden;
    @FXML private TextField txtCliente;
    @FXML private TextField txtTotal;
    @FXML private TextField txtBuscar;

    @FXML private ComboBox<String> cmbMetodoPago;
    @FXML private ComboBox<String> cmbEstado;

    @FXML private TableView<Factura> tablaFacturas;

    @FXML private TableColumn<Factura, Integer> colId;
    @FXML private TableColumn<Factura, Integer> colIdOrden;
    @FXML private TableColumn<Factura, String> colCliente;
    @FXML private TableColumn<Factura, String> colMetodoPago;
    @FXML private TableColumn<Factura, Double> colTotal;
    @FXML private TableColumn<Factura, String> colEstado;
    @FXML private TableColumn<Factura, String> colFecha;

    private final FacturaDAO facturaDAO = new FacturaDAO();
    private Factura facturaSeleccionada;

    @FXML
    public void initialize() {

        colId.setCellValueFactory(new PropertyValueFactory<>("idFactura"));
        colIdOrden.setCellValueFactory(new PropertyValueFactory<>("idOrden"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colMetodoPago.setCellValueFactory(new PropertyValueFactory<>("metodoPago"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaFactura"));
        
        colEstado.setCellFactory(column -> new TableCell<Factura, String>() {
        @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);

                if (empty || estado == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(estado);

                    switch (estado) {
                        case "PAGADA":
                            setStyle("-fx-background-color:#DCFCE7; -fx-text-fill:#166534;");
                            break;
                        case "PENDIENTE":
                            setStyle("-fx-background-color:#FEF3C7; -fx-text-fill:#92400E;");
                            break;
                        case "ANULADA":
                            setStyle("-fx-background-color:#FEE2E2; -fx-text-fill:#991B1B;");
                            break;
                        default:
                            setStyle("");
                            break;
                    }
                }
            }
        });

        cmbMetodoPago.getItems().addAll(
                "EFECTIVO",
                "TRANSFERENCIA",
                "TARJETA",
                "QR"
        );

        cmbEstado.getItems().addAll(
                "PAGADA",
                "PENDIENTE",
                "ANULADA"
        );

        cmbEstado.setValue("PAGADA");

        cargarOrdenesParaFacturar();
        listarFacturas();

        tablaFacturas.getSelectionModel().selectedItemProperty().addListener(
                (obs, anterior, factura) -> {
                    if (factura != null) {
                        facturaSeleccionada = factura;
                    }
                }
        );
    }

    @FXML
    public void cargarDatosOrden() {

        OrdenFacturacion orden = cmbOrden.getValue();

        if (orden != null) {
            txtCliente.setText(orden.getNombreCliente());
            txtTotal.setText(String.valueOf(orden.getTotal()));
        }
    }

    @FXML
    public void facturar() {

        OrdenFacturacion ordenSeleccionada = cmbOrden.getValue();

        if (ordenSeleccionada == null
                || txtTotal.getText().isBlank()
                || cmbMetodoPago.getValue() == null
                || cmbEstado.getValue() == null) {

            mostrarMensaje(
                    "Campos obligatorios",
                    "Seleccione una orden, método de pago y estado.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        Factura factura = new Factura();

        factura.setIdOrden(ordenSeleccionada.getIdOrden());
        factura.setIdCliente(ordenSeleccionada.getIdCliente());
        factura.setTotal(ordenSeleccionada.getTotal());
        factura.setMetodoPago(cmbMetodoPago.getValue());
        factura.setEstado(cmbEstado.getValue());

        boolean guardado = facturaDAO.insertar(factura);

        if (guardado) {
            mostrarMensaje(
                    "Factura registrada",
                    "La factura fue registrada correctamente.",
                    Alert.AlertType.INFORMATION
            );

            limpiar();
            cargarOrdenesParaFacturar();
            listarFacturas();

        } else {
            mostrarMensaje(
                    "Error",
                    "No se pudo registrar la factura.",
                    Alert.AlertType.ERROR
            );
        }
    }

    @FXML
    public void eliminar() {

        if (facturaSeleccionada == null) {
            mostrarMensaje(
                    "Eliminar factura",
                    "Seleccione una factura de la tabla.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Seguro que desea eliminar esta factura?");

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        boolean eliminado =
                facturaDAO.eliminar(facturaSeleccionada.getIdFactura());

        if (eliminado) {
            mostrarMensaje(
                    "Factura eliminada",
                    "La factura fue eliminada correctamente.",
                    Alert.AlertType.INFORMATION
            );

            limpiar();
            listarFacturas();
            facturaSeleccionada = null;

        } else {
            mostrarMensaje(
                    "Error",
                    "No se pudo eliminar la factura.",
                    Alert.AlertType.ERROR
            );
        }
    }

    @FXML
    public void limpiar() {
        cmbOrden.setValue(null);
        txtCliente.clear();
        txtTotal.clear();
        cmbMetodoPago.setValue(null);
        cmbEstado.setValue("PAGADA");

        tablaFacturas.getSelectionModel().clearSelection();
        facturaSeleccionada = null;
    }

    private void cargarOrdenesParaFacturar() {
        cmbOrden.setItems(
                FXCollections.observableArrayList(
                        facturaDAO.listarOrdenesParaFacturar()
                )
        );
    }

    private void listarFacturas() {

        ObservableList<Factura> lista =
                FXCollections.observableArrayList(
                        facturaDAO.listar()
                );

        FilteredList<Factura> listaFiltrada =
                new FilteredList<>(lista, p -> true);

        txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> {

            listaFiltrada.setPredicate(factura -> {

                if (newValue == null || newValue.isBlank()) {
                    return true;
                }

                String filtro = newValue.toLowerCase();

                return contiene(factura.getNombreCliente(), filtro)
                        || contiene(factura.getMetodoPago(), filtro)
                        || contiene(factura.getEstado(), filtro)
                        || contiene(factura.getFechaFactura(), filtro)
                        || contiene(String.valueOf(factura.getIdOrden()), filtro);
            });
        });

        SortedList<Factura> listaOrdenada =
                new SortedList<>(listaFiltrada);

        listaOrdenada.comparatorProperty().bind(
                tablaFacturas.comparatorProperty()
        );

        tablaFacturas.setItems(listaOrdenada);
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
    @FXML
    public void exportarExcel() {

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet hoja = workbook.createSheet("Facturas");

            Row encabezado = hoja.createRow(0);
            encabezado.createCell(0).setCellValue("ID");
            encabezado.createCell(1).setCellValue("Orden");
            encabezado.createCell(2).setCellValue("Cliente");
            encabezado.createCell(3).setCellValue("Método Pago");
            encabezado.createCell(4).setCellValue("Total");
            encabezado.createCell(5).setCellValue("Estado");
            encabezado.createCell(6).setCellValue("Fecha");

            int fila = 1;

            for (Factura factura : tablaFacturas.getItems()) {
                Row row = hoja.createRow(fila++);

                row.createCell(0).setCellValue(factura.getIdFactura());
                row.createCell(1).setCellValue(factura.getIdOrden());
                row.createCell(2).setCellValue(factura.getNombreCliente());
                row.createCell(3).setCellValue(factura.getMetodoPago());
                row.createCell(4).setCellValue(factura.getTotal());
                row.createCell(5).setCellValue(factura.getEstado());
                row.createCell(6).setCellValue(factura.getFechaFactura());
            }

            for (int i = 0; i <= 6; i++) {
                hoja.autoSizeColumn(i);
            }

            FileOutputStream archivo = new FileOutputStream("facturas.xlsx");
            workbook.write(archivo);
            archivo.close();

            mostrarMensaje("Excel generado", "Archivo facturas.xlsx generado correctamente.", Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            mostrarMensaje("Error", "No se pudo exportar facturas a Excel.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void generarPdf() {

        if (facturaSeleccionada == null) {
            mostrarMensaje(
                    "PDF Factura",
                    "Seleccione una factura de la tabla.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        try {
            Document documento = new Document();
            PdfWriter.getInstance(
                    documento,
                    new FileOutputStream("factura_" + facturaSeleccionada.getIdFactura() + ".pdf")
            );

            documento.open();

            com.lowagie.text.Font titulo =
                    new com.lowagie.text.Font(
                            com.lowagie.text.Font.HELVETICA,
                            20,
                            com.lowagie.text.Font.BOLD);

            com.lowagie.text.Font subtitulo =
                    new com.lowagie.text.Font(
                            com.lowagie.text.Font.HELVETICA,
                            14,
                            com.lowagie.text.Font.BOLD);

            com.lowagie.text.Font normal =
                    new com.lowagie.text.Font(
                            com.lowagie.text.Font.HELVETICA,
                            12);

            Paragraph encabezado = new Paragraph("TECNOREPARA", titulo);
            encabezado.setAlignment(Element.ALIGN_CENTER);
            documento.add(encabezado);

            Paragraph sub = new Paragraph("Factura de Servicio Técnico\n\n", subtitulo);
            sub.setAlignment(Element.ALIGN_CENTER);
            documento.add(sub);

            documento.add(new Paragraph("Factura N°: " + facturaSeleccionada.getIdFactura(), normal));
            documento.add(new Paragraph("Orden N°: " + facturaSeleccionada.getIdOrden(), normal));
            documento.add(new Paragraph("Cliente: " + facturaSeleccionada.getNombreCliente(), normal));
            documento.add(new Paragraph("Método de pago: " + facturaSeleccionada.getMetodoPago(), normal));
            documento.add(new Paragraph("Estado: " + facturaSeleccionada.getEstado(), normal));
            documento.add(new Paragraph("Fecha: " + facturaSeleccionada.getFechaFactura(), normal));
            documento.add(new Paragraph("\n"));

           Paragraph total = new Paragraph(
                    "TOTAL: Gs. " + String.format("%,.0f", facturaSeleccionada.getTotal()),
                    new com.lowagie.text.Font(
                            com.lowagie.text.Font.HELVETICA,
                            18,
                            com.lowagie.text.Font.BOLD
                    )
            );

            total.setAlignment(Element.ALIGN_RIGHT);
            documento.add(total);

            documento.add(new Paragraph("\n\n"));
            documento.add(new Paragraph("Firma: _______________________________", normal));

            documento.close();

            mostrarMensaje("Error", "No se pudo generar el PDF del presupuesto.", Alert.AlertType.ERROR);

        } catch (Exception e) {
            mostrarMensaje(
                    "Error",
                    "No se pudo generar el PDF de factura.",
                    Alert.AlertType.ERROR
            );
        }
    }

}