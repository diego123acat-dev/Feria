package com.example.model;

public class Proceso {
    private final int id;
    private final int tiempoLlegada;
    private final int tiempoEjecucion;
    private int tiempoRestante;
    private EstadoProceso estado;

    public Proceso(int id, int tiempoLlegada, int tiempoEjecucion) {
        this.id = id;
        this.tiempoLlegada = tiempoLlegada;
        this.tiempoEjecucion = tiempoEjecucion;
        this.tiempoRestante = tiempoEjecucion;
        this.estado = EstadoProceso.NUEVO;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public int getTiempoLlegada() {
        return tiempoLlegada;
    }

    public int getTiempoEjecucion() {
        return tiempoEjecucion;
    }

    public int getTiempoRestante() {
        return tiempoRestante;
    }
    
    public EstadoProceso getEstado() {
        return estado;
    }
    public void setTiempoRestante(int tiempoRestante) {
        this.tiempoRestante = tiempoRestante;
    }

    public void setEstado(EstadoProceso estado) {
        this.estado = estado;
    }

    public void disminuirTiempoRestante(int tiempo) {
        this.tiempoRestante -= tiempo;
        if (this.tiempoRestante <= 0) {
            this.tiempoRestante = 0;
            this.estado = EstadoProceso.TERMINATED;
        }
    }
}
