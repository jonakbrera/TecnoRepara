package com.tecnorepara.model;

public class Orden {

    private int idOrden;
    private int idCliente;
    private String nombreCliente;

    private String dispositivo;
    private String marca;
    private String modelo;

    private String problemaReportado;
    private String diagnostico;
    private String trabajoRealizado;

    private String estado;

    private String fechaIngreso;
    private String fechaPresupuesto;
    private String fechaReparacion;
    private String fechaEntrega;

    public Orden() {
    }

    public int getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(int idOrden) {
        this.idOrden = idOrden;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getDispositivo() {
        return dispositivo;
    }

    public void setDispositivo(String dispositivo) {
        this.dispositivo = dispositivo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getProblemaReportado() {
        return problemaReportado;
    }

    public void setProblemaReportado(String problemaReportado) {
        this.problemaReportado = problemaReportado;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getTrabajoRealizado() {
        return trabajoRealizado;
    }

    public void setTrabajoRealizado(String trabajoRealizado) {
        this.trabajoRealizado = trabajoRealizado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(String fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getFechaPresupuesto() {
        return fechaPresupuesto;
    }

    public void setFechaPresupuesto(String fechaPresupuesto) {
        this.fechaPresupuesto = fechaPresupuesto;
    }

    public String getFechaReparacion() {
        return fechaReparacion;
    }

    public void setFechaReparacion(String fechaReparacion) {
        this.fechaReparacion = fechaReparacion;
    }

    public String getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(String fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }
    
    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }
    @Override
    public String toString() {
        return idOrden + " - " + dispositivo + " " + marca + " " + modelo + " - " + nombreCliente;
    }

}