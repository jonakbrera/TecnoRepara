package com.tecnorepara.controller;

import com.tecnorepara.dao.ClienteDAO;
import com.tecnorepara.dao.OrdenDAO;
import com.tecnorepara.model.Cliente;
import com.tecnorepara.model.Orden;

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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import java.io.FileOutputStream;

public class OrdenController {

    @FXML private ComboBox<Cliente> cmbCliente;
    @FXML private ComboBox<String> cmbEstado;

    @FXML private TextField txtDispositivo;
    @FXML private TextField txtMarca;
    @FXML private TextField txtModelo;
    @FXML private TextArea txtProblemaReportado;
    @FXML private TextArea txtDiagnostico;
    @FXML private TextArea txtTrabajoRealizado;
    @FXML private TextField txtBuscar;

    @FXML private TableView<Orden> tablaOrdenes;

    @FXML private TableColumn<Orden, Integer> colId;
    @FXML private TableColumn<Orden, String> colCliente;
    @FXML private TableColumn<Orden, String> colDispositivo;
    @FXML private TableColumn<Orden, String> colMarca;
    @FXML private TableColumn<Orden, String> colModelo;
    @FXML private TableColumn<Orden, String> colProblema;
    @FXML private TableColumn<Orden, String> colEstado;
    @FXML private TableColumn<Orden, String> colFechaIngreso;

    private final OrdenDAO ordenDAO = new OrdenDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();

    private Orden ordenSeleccionada;

    @FXML
    public void initialize() {

        colId.setCellValueFactory(new PropertyValueFactory<>("idOrden"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colDispositivo.setCellValueFactory(new PropertyValueFactory<>("dispositivo"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colProblema.setCellValueFactory(new PropertyValueFactory<>("problemaReportado"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colFechaIngreso.setCellValueFactory(new PropertyValueFactory<>("fechaIngreso"));
        colEstado.setCellFactory(column -> new TableCell<Orden, String>() {
        @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);

                if (empty || estado == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(estado);

                    switch (estado) {
                        case "PENDIENTE":
                            setStyle("-fx-background-color:#FEF3C7; -fx-text-fill:#92400E;");
                            break;
                        case "EN DIAGNOSTICO":
                            setStyle("-fx-background-color:#DBEAFE; -fx-text-fill:#1E40AF;");
                            break;
                        case "PRESUPUESTADO":
                            setStyle("-fx-background-color:#EDE9FE; -fx-text-fill:#5B21B6;");
                            break;
                        case "EN REPARACION":
                            setStyle("-fx-background-color:#FFEDD5; -fx-text-fill:#9A3412;");
                            break;
                        case "REPARADO":
                            setStyle("-fx-background-color:#DCFCE7; -fx-text-fill:#166534;");
                            break;
                        case "ENTREGADO":
                            setStyle("-fx-background-color:#BBF7D0; -fx-text-fill:#14532D;");
                            break;
                        case "CANCELADO":
                            setStyle("-fx-background-color:#FEE2E2; -fx-text-fill:#991B1B;");
                            break;
                        default:
                            setStyle("");
                            break;
                    }
                }
            }
        });
        cmbEstado.getItems().addAll(
                "PENDIENTE",
                "EN DIAGNOSTICO",
                "PRESUPUESTADO",
                "EN REPARACION",
                "REPARADO",
                "ENTREGADO",
                "CANCELADO"
        );

        cmbCliente.setItems(
                FXCollections.observableArrayList(
                        clienteDAO.listar()
                )
        );

        listarOrdenes();

        tablaOrdenes.getSelectionModel().selectedItemProperty().addListener(
                (obs, anterior, orden) -> {
                    if (orden != null) {
                        ordenSeleccionada = orden;
                        cargarOrdenEnFormulario(orden);
                    }
                }
        );
    }

    @FXML
    public void guardar() {

        if (cmbCliente.getValue() == null
                || txtDispositivo.getText().isBlank()
                || txtProblemaReportado.getText().isBlank()
                || cmbEstado.getValue() == null) {

            mostrarMensaje(
                    "Campos obligatorios",
                    "Cliente, dispositivo, problema reportado y estado son obligatorios.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        Orden orden = obtenerOrdenDesdeFormulario();

        boolean guardado = ordenDAO.insertar(orden);

        if (guardado) {
            mostrarMensaje(
                    "Orden guardada",
                    "La orden fue registrada correctamente.",
                    Alert.AlertType.INFORMATION
            );

            limpiar();
            listarOrdenes();

        } else {
            mostrarMensaje(
                    "Error",
                    "No se pudo guardar la orden.",
                    Alert.AlertType.ERROR
            );
        }
    }

    @FXML
    public void modificar() {

        if (ordenSeleccionada == null) {
            mostrarMensaje(
                    "Modificar orden",
                    "Seleccione una orden de la tabla.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        if (cmbCliente.getValue() == null
                || txtDispositivo.getText().isBlank()
                || txtProblemaReportado.getText().isBlank()
                || cmbEstado.getValue() == null) {

            mostrarMensaje(
                    "Campos obligatorios",
                    "Cliente, dispositivo, problema reportado y estado son obligatorios.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        Orden orden = obtenerOrdenDesdeFormulario();
        orden.setIdOrden(ordenSeleccionada.getIdOrden());

        boolean actualizado = ordenDAO.actualizar(orden);

        if (actualizado) {
            mostrarMensaje(
                    "Orden actualizada",
                    "La orden fue actualizada correctamente.",
                    Alert.AlertType.INFORMATION
            );

            limpiar();
            listarOrdenes();

        } else {
            mostrarMensaje(
                    "Error",
                    "No se pudo actualizar la orden.",
                    Alert.AlertType.ERROR
            );
        }
    }

    @FXML
    public void eliminar() {

        if (ordenSeleccionada == null) {
            mostrarMensaje(
                    "Eliminar orden",
                    "Seleccione una orden de la tabla.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Seguro que desea eliminar esta orden?");

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        boolean eliminado = ordenDAO.eliminar(ordenSeleccionada.getIdOrden());

        if (eliminado) {
            mostrarMensaje(
                    "Orden eliminada",
                    "La orden fue eliminada correctamente.",
                    Alert.AlertType.INFORMATION
            );

            limpiar();
            listarOrdenes();

        } else {
            mostrarMensaje(
                    "Error",
                    "No se pudo eliminar la orden.",
                    Alert.AlertType.ERROR
            );
        }
    }

    @FXML
    public void limpiar() {
        cmbCliente.setValue(null);
        cmbEstado.setValue(null);

        txtDispositivo.clear();
        txtMarca.clear();
        txtModelo.clear();
        txtProblemaReportado.clear();
        txtDiagnostico.clear();
        txtTrabajoRealizado.clear();

        tablaOrdenes.getSelectionModel().clearSelection();
        ordenSeleccionada = null;
    }

    private Orden obtenerOrdenDesdeFormulario() {

        Orden orden = new Orden();

        orden.setIdCliente(cmbCliente.getValue().getIdCliente());
        orden.setDispositivo(txtDispositivo.getText().trim().toUpperCase());
        orden.setMarca(txtMarca.getText().trim().toUpperCase());
        orden.setModelo(txtModelo.getText().trim().toUpperCase());
        orden.setProblemaReportado(txtProblemaReportado.getText().trim().toUpperCase());
        orden.setDiagnostico(txtDiagnostico.getText().trim().toUpperCase());
        orden.setTrabajoRealizado(txtTrabajoRealizado.getText().trim().toUpperCase());
        orden.setEstado(cmbEstado.getValue());

        return orden;
    }

    private void cargarOrdenEnFormulario(Orden orden) {

        for (Cliente cliente : cmbCliente.getItems()) {
            if (cliente.getIdCliente() == orden.getIdCliente()) {
                cmbCliente.setValue(cliente);
                break;
            }
        }

        cmbEstado.setValue(orden.getEstado());

        txtDispositivo.setText(orden.getDispositivo());
        txtMarca.setText(orden.getMarca());
        txtModelo.setText(orden.getModelo());
        txtProblemaReportado.setText(orden.getProblemaReportado());
        txtDiagnostico.setText(orden.getDiagnostico());
        txtTrabajoRealizado.setText(orden.getTrabajoRealizado());
    }

    private void listarOrdenes() {

        ObservableList<Orden> lista =
                FXCollections.observableArrayList(
                        ordenDAO.listar()
                );

        FilteredList<Orden> listaFiltrada =
                new FilteredList<>(lista, p -> true);

        txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> {

            listaFiltrada.setPredicate(orden -> {

                if (newValue == null || newValue.isBlank()) {
                    return true;
                }

                String filtro = newValue.toLowerCase();

                return contiene(orden.getNombreCliente(), filtro)
                        || contiene(orden.getDispositivo(), filtro)
                        || contiene(orden.getMarca(), filtro)
                        || contiene(orden.getModelo(), filtro)
                        || contiene(orden.getProblemaReportado(), filtro)
                        || contiene(orden.getEstado(), filtro)
                        || contiene(orden.getFechaIngreso(), filtro);
            });
        });

        SortedList<Orden> listaOrdenada =
                new SortedList<>(listaFiltrada);

        listaOrdenada.comparatorProperty().bind(
                tablaOrdenes.comparatorProperty()
        );

        tablaOrdenes.setItems(listaOrdenada);
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

            Sheet hoja = workbook.createSheet("Ordenes");

            Row encabezado = hoja.createRow(0);
            encabezado.createCell(0).setCellValue("ID");
            encabezado.createCell(1).setCellValue("Cliente");
            encabezado.createCell(2).setCellValue("Dispositivo");
            encabezado.createCell(3).setCellValue("Marca");
            encabezado.createCell(4).setCellValue("Modelo");
            encabezado.createCell(5).setCellValue("Problema");
            encabezado.createCell(6).setCellValue("Estado");
            encabezado.createCell(7).setCellValue("Fecha Ingreso");

            int fila = 1;

            for (Orden orden : tablaOrdenes.getItems()) {
                Row row = hoja.createRow(fila++);

                row.createCell(0).setCellValue(orden.getIdOrden());
                row.createCell(1).setCellValue(orden.getNombreCliente());
                row.createCell(2).setCellValue(orden.getDispositivo());
                row.createCell(3).setCellValue(orden.getMarca());
                row.createCell(4).setCellValue(orden.getModelo());
                row.createCell(5).setCellValue(orden.getProblemaReportado());
                row.createCell(6).setCellValue(orden.getEstado());
                row.createCell(7).setCellValue(orden.getFechaIngreso());
            }

            for (int i = 0; i <= 7; i++) {
                hoja.autoSizeColumn(i);
            }

            FileOutputStream archivo = new FileOutputStream("ordenes.xlsx");
            workbook.write(archivo);
            archivo.close();

            mostrarMensaje("Excel generado", "Archivo ordenes.xlsx generado correctamente.", Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            mostrarMensaje("Error", "No se pudo exportar órdenes a Excel.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void generarPdf() {

        if (ordenSeleccionada == null) {
            mostrarMensaje("PDF Orden", "Seleccione una orden de la tabla.", Alert.AlertType.WARNING);
            return;
        }

        try {
            Document documento = new Document();

            PdfWriter.getInstance(
                    documento,
                    new FileOutputStream("orden_" + ordenSeleccionada.getIdOrden() + ".pdf")
            );

            documento.open();

            com.lowagie.text.Font titulo =
                    new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 20, com.lowagie.text.Font.BOLD);

            com.lowagie.text.Font subtitulo =
                    new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 14, com.lowagie.text.Font.BOLD);

            com.lowagie.text.Font normal =
                    new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 12);

            Paragraph encabezado = new Paragraph("TECNOREPARA", titulo);
            encabezado.setAlignment(Element.ALIGN_CENTER);
            documento.add(encabezado);

            Paragraph sub = new Paragraph("Orden de Reparación\n\n", subtitulo);
            sub.setAlignment(Element.ALIGN_CENTER);
            documento.add(sub);

            documento.add(new Paragraph("Orden N°: " + ordenSeleccionada.getIdOrden(), normal));
            documento.add(new Paragraph("Cliente: " + ordenSeleccionada.getNombreCliente(), normal));
            documento.add(new Paragraph("Dispositivo: " + ordenSeleccionada.getDispositivo(), normal));
            documento.add(new Paragraph("Marca: " + ordenSeleccionada.getMarca(), normal));
            documento.add(new Paragraph("Modelo: " + ordenSeleccionada.getModelo(), normal));
            documento.add(new Paragraph("Estado: " + ordenSeleccionada.getEstado(), normal));
            documento.add(new Paragraph("Fecha ingreso: " + ordenSeleccionada.getFechaIngreso(), normal));
            documento.add(new Paragraph("\n"));

            documento.add(new Paragraph("Problema reportado:", subtitulo));
            documento.add(new Paragraph(ordenSeleccionada.getProblemaReportado(), normal));

            documento.add(new Paragraph("\nDiagnóstico:", subtitulo));
            documento.add(new Paragraph(ordenSeleccionada.getDiagnostico(), normal));

            documento.add(new Paragraph("\nTrabajo realizado:", subtitulo));
            documento.add(new Paragraph(ordenSeleccionada.getTrabajoRealizado(), normal));

            documento.add(new Paragraph("\n\nFirma cliente: _______________________________", normal));
            documento.add(new Paragraph("Firma técnico: _______________________________", normal));

            documento.close();

            mostrarMensaje("Error", "No se pudo generar el PDF del presupuesto.", Alert.AlertType.ERROR);

        } catch (Exception e) {
            mostrarMensaje("Error", "No se pudo generar el PDF de la orden.", Alert.AlertType.ERROR);
        }
    }
}