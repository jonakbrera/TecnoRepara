package com.tecnorepara.controller;

import com.tecnorepara.dao.PresupuestoDAO;
import com.tecnorepara.model.Presupuesto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.tecnorepara.dao.OrdenDAO;
import com.tecnorepara.model.Orden;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import java.io.FileOutputStream;

public class PresupuestoController {

    @FXML private ComboBox<Orden> cmbOrden;
    @FXML private TextField txtManoObra;
    @FXML private TextField txtTotalRepuestos;
    @FXML private TextField txtTotal;
    @FXML private ComboBox<String> cmbEstado;

    @FXML private TableView<Presupuesto> tablaPresupuestos;

    @FXML private TableColumn<Presupuesto, Integer> colId;
    @FXML private TableColumn<Presupuesto, String> colDescripcionOrden;
    @FXML private TableColumn<Presupuesto, Double> colManoObra;
    @FXML private TableColumn<Presupuesto, Double> colTotalRepuestos;
    @FXML private TableColumn<Presupuesto, Double> colTotal;
    @FXML private TableColumn<Presupuesto, String> colEstado;
    @FXML private TableColumn<Presupuesto, String> colFechaPresupuesto;
    @FXML private TextField txtBuscar;

    private final PresupuestoDAO presupuestoDAO = new PresupuestoDAO();
    private final OrdenDAO ordenDAO = new OrdenDAO();
    private Presupuesto presupuestoSeleccionado;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idPresupuesto"));
        colDescripcionOrden.setCellValueFactory(new PropertyValueFactory<>("descripcionOrden"));
        colManoObra.setCellValueFactory(new PropertyValueFactory<>("manoObra"));
        colTotalRepuestos.setCellValueFactory(new PropertyValueFactory<>("totalRepuestos"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colFechaPresupuesto.setCellValueFactory(new PropertyValueFactory<>("fechaPresupuesto"));
        
        colEstado.setCellFactory(column -> new TableCell<Presupuesto, String>() {
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
                        case "APROBADO":
                            setStyle("-fx-background-color:#DCFCE7; -fx-text-fill:#166534;");
                            break;
                        case "RECHAZADO":
                            setStyle("-fx-background-color:#FEE2E2; -fx-text-fill:#991B1B;");
                            break;
                        case "FACTURADO":
                            setStyle("-fx-background-color:#DBEAFE; -fx-text-fill:#1E40AF;");
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
                "APROBADO",
                "RECHAZADO",
                "FACTURADO"
        );
        cmbOrden.setItems(
        FXCollections.observableArrayList(
                ordenDAO.listarPendientesPresupuesto()
        )
        );
        txtManoObra.textProperty().addListener((obs, oldValue, newValue) -> calcularTotalAutomatico());
        txtTotalRepuestos.textProperty().addListener((obs, oldValue, newValue) -> calcularTotalAutomatico());
        listarPresupuestos();
    }

    @FXML private void calcularTotalAutomatico() {
        try {
            double manoObra = txtManoObra.getText().isBlank()
                    ? 0
                    : Double.parseDouble(txtManoObra.getText());

            double repuestos = txtTotalRepuestos.getText().isBlank()
                    ? 0
                    : Double.parseDouble(txtTotalRepuestos.getText());

            double total = manoObra + repuestos;

            txtTotal.setText(String.valueOf(total));

        } catch (NumberFormatException e) {
            txtTotal.setText("");
        }
    }

    @FXML
    public void guardar() {

        if (cmbOrden.getValue() == null
                || txtManoObra.getText().isBlank()
                || txtTotalRepuestos.getText().isBlank()
                || cmbEstado.getValue() == null) {

            mostrarMensaje(
                    "Campos obligatorios",
                    "Orden, mano de obra, repuestos y estado son obligatorios.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        try {
            Presupuesto presupuesto = obtenerPresupuestoDesdeFormulario();

            boolean guardado = presupuestoDAO.insertar(presupuesto);

            if (guardado) {
                mostrarMensaje(
                        "Presupuesto guardado",
                        "El presupuesto fue registrado correctamente.",
                        Alert.AlertType.INFORMATION
                );

                limpiar();
                listarPresupuestos();

            } else {
                mostrarMensaje(
                        "Error",
                        "No se pudo guardar el presupuesto. Verifique el ID de la orden.",
                        Alert.AlertType.ERROR
                );
            }

        } catch (NumberFormatException e) {
            mostrarMensaje(
                    "Datos inválidos",
                    "Verifique ID Orden, mano de obra y repuestos.",
                    Alert.AlertType.ERROR
            );
        }
    }

    @FXML
    public void modificar() {

        if (presupuestoSeleccionado == null) {
            mostrarMensaje(
                    "Modificar presupuesto",
                    "Seleccione un presupuesto de la tabla.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        try {
            Presupuesto presupuesto = obtenerPresupuestoDesdeFormulario();
            presupuesto.setIdPresupuesto(presupuestoSeleccionado.getIdPresupuesto());

            boolean actualizado = presupuestoDAO.actualizar(presupuesto);

            if (actualizado) {
                mostrarMensaje(
                        "Presupuesto actualizado",
                        "El presupuesto fue actualizado correctamente.",
                        Alert.AlertType.INFORMATION
                );

                limpiar();
                listarPresupuestos();
                presupuestoSeleccionado = null;

            } else {
                mostrarMensaje(
                        "Error",
                        "No se pudo actualizar el presupuesto.",
                        Alert.AlertType.ERROR
                );
            }

        } catch (NumberFormatException e) {
            mostrarMensaje(
                    "Datos inválidos",
                    "Verifique ID Orden, mano de obra y repuestos.",
                    Alert.AlertType.ERROR
            );
        }
    }

    @FXML
    public void eliminar() {

        if (presupuestoSeleccionado == null) {
            mostrarMensaje(
                    "Eliminar presupuesto",
                    "Seleccione un presupuesto de la tabla.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Seguro que desea eliminar este presupuesto?");

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        boolean eliminado =
                presupuestoDAO.eliminar(
                        presupuestoSeleccionado.getIdPresupuesto()
                );

        if (eliminado) {

            mostrarMensaje(
                    "Presupuesto eliminado",
                    "El presupuesto fue eliminado correctamente.",
                    Alert.AlertType.INFORMATION
            );

            limpiar();
            listarPresupuestos();

        } else {

            mostrarMensaje(
                    "Error",
                    "No se pudo eliminar el presupuesto.",
                    Alert.AlertType.ERROR
            );
        }
    }

    @FXML
    public void limpiar() {
        cmbOrden.setValue(null);
        txtManoObra.clear();
        txtTotalRepuestos.clear();
        txtTotal.clear();
        cmbEstado.setValue(null);

        presupuestoSeleccionado = null;
    }

    private Presupuesto obtenerPresupuestoDesdeFormulario() {

        double manoObra = Double.parseDouble(txtManoObra.getText());
        double repuestos = Double.parseDouble(txtTotalRepuestos.getText());
        double total = manoObra + repuestos;

        txtTotal.setText(String.valueOf(total));

        Presupuesto presupuesto = new Presupuesto();

        presupuesto.setIdOrden(cmbOrden.getValue().getIdOrden());
        presupuesto.setManoObra(manoObra);
        presupuesto.setTotalRepuestos(repuestos);
        presupuesto.setTotal(total);
        presupuesto.setEstado(cmbEstado.getValue());

        return presupuesto;
    }

    private void listarPresupuestos() {
    tablaPresupuestos.getSelectionModel().selectedItemProperty().addListener(
            (obs, anterior, presupuesto) -> {

                if (presupuesto != null) {

                    presupuestoSeleccionado = presupuesto;

                    for (Orden orden : cmbOrden.getItems()) {

                        if (orden.getIdOrden() == presupuesto.getIdOrden()) {
                            cmbOrden.setValue(orden);
                            break;
                        }
                    }

                    txtManoObra.setText(
                            String.valueOf(presupuesto.getManoObra())
                    );

                    txtTotalRepuestos.setText(
                            String.valueOf(presupuesto.getTotalRepuestos())
                    );

                    txtTotal.setText(
                            String.valueOf(presupuesto.getTotal())
                    );

                    cmbEstado.setValue(
                            presupuesto.getEstado()
                    );
                }

            });
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

            Sheet hoja = workbook.createSheet("Presupuestos");

            Row encabezado = hoja.createRow(0);
            encabezado.createCell(0).setCellValue("ID");
            encabezado.createCell(1).setCellValue("Orden");
            encabezado.createCell(2).setCellValue("Mano Obra");
            encabezado.createCell(3).setCellValue("Repuestos");
            encabezado.createCell(4).setCellValue("Total");
            encabezado.createCell(5).setCellValue("Estado");
            encabezado.createCell(6).setCellValue("Fecha");

            int fila = 1;

            for (Presupuesto presupuesto : tablaPresupuestos.getItems()) {
                Row row = hoja.createRow(fila++);

                row.createCell(0).setCellValue(presupuesto.getIdPresupuesto());
                row.createCell(1).setCellValue(presupuesto.getDescripcionOrden());
                row.createCell(2).setCellValue(presupuesto.getManoObra());
                row.createCell(3).setCellValue(presupuesto.getTotalRepuestos());
                row.createCell(4).setCellValue(presupuesto.getTotal());
                row.createCell(5).setCellValue(presupuesto.getEstado());
                row.createCell(6).setCellValue(presupuesto.getFechaPresupuesto());
            }

            for (int i = 0; i <= 6; i++) {
                hoja.autoSizeColumn(i);
            }

            FileOutputStream archivo = new FileOutputStream("presupuestos.xlsx");
            workbook.write(archivo);
            archivo.close();

            mostrarMensaje("Excel generado", "Archivo presupuestos.xlsx generado correctamente.", Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            mostrarMensaje("Error", "No se pudo exportar presupuestos a Excel.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void generarPdf() {

        if (presupuestoSeleccionado == null) {
            mostrarMensaje("PDF Presupuesto", "Seleccione un presupuesto de la tabla.", Alert.AlertType.WARNING);
            return;
        }

        try {
            Document documento = new Document();

            PdfWriter.getInstance(
                    documento,
                    new FileOutputStream("presupuesto_" + presupuestoSeleccionado.getIdPresupuesto() + ".pdf")
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

            Paragraph sub = new Paragraph("Presupuesto de Reparación\n\n", subtitulo);
            sub.setAlignment(Element.ALIGN_CENTER);
            documento.add(sub);

            documento.add(new Paragraph("Presupuesto N°: " + presupuestoSeleccionado.getIdPresupuesto(), normal));
            documento.add(new Paragraph("Orden: " + presupuestoSeleccionado.getDescripcionOrden(), normal));
            documento.add(new Paragraph("Estado: " + presupuestoSeleccionado.getEstado(), normal));
            documento.add(new Paragraph("Fecha: " + presupuestoSeleccionado.getFechaPresupuesto(), normal));
            documento.add(new Paragraph("\n"));

            documento.add(new Paragraph("Mano de obra: Gs. " + String.format("%,.0f", presupuestoSeleccionado.getManoObra()), normal));
            documento.add(new Paragraph("Repuestos: Gs. " + String.format("%,.0f", presupuestoSeleccionado.getTotalRepuestos()), normal));
            documento.add(new Paragraph("\n"));

            Paragraph total = new Paragraph(
                    "TOTAL: Gs. " + String.format("%,.0f", presupuestoSeleccionado.getTotal()),
                    new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 18, com.lowagie.text.Font.BOLD)
            );

            total.setAlignment(Element.ALIGN_RIGHT);
            documento.add(total);

            documento.add(new Paragraph("\n\nFirma: _______________________________", normal));

            documento.close();

            mostrarMensaje("PDF generado", "Archivo generado en la carpeta del proyecto.",Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            mostrarMensaje("Error", "No se pudo generar el PDF del presupuesto.", Alert.AlertType.ERROR);
        }
    }
    
}