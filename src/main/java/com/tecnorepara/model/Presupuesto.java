package com.tecnorepara.model;

public class Presupuesto {

    private int idPresupuesto;
    private int idOrden;

    private double manoObra;
    private double totalRepuestos;
    private double total;

    private String estado;
    private String fechaPresupuesto;
    private String descripcionOrden;
    public Presupuesto() {
    }

    public int getIdPresupuesto() {
        return idPresupuesto;
    }

    public void setIdPresupuesto(int idPresupuesto) {
        this.idPresupuesto = idPresupuesto;
    }

    public int getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(int idOrden) {
        this.idOrden = idOrden;
    }

    public double getManoObra() {
        return manoObra;
    }

    public void setManoObra(double manoObra) {
        this.manoObra = manoObra;
    }

    public double getTotalRepuestos() {
        return totalRepuestos;
    }

    public void setTotalRepuestos(double totalRepuestos) {
        this.totalRepuestos = totalRepuestos;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFechaPresupuesto() {
        return fechaPresupuesto;
    }

    public void setFechaPresupuesto(String fechaPresupuesto) {
        this.fechaPresupuesto = fechaPresupuesto;
    }
    public String getDescripcionOrden() {
        return descripcionOrden;
    }

    public void setDescripcionOrden(String descripcionOrden) {
        this.descripcionOrden = descripcionOrden;
    }
}
    
