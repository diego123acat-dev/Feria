package com.example.model;

public class Recurso {

    private final String nombre;
    private boolean ocupado;
    private Proceso propietario;

    public Recurso(String nombre) {
        this.nombre = nombre;
        this.ocupado = false;
        this.propietario = null;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isOcupado() {
        return ocupado;
    }

    public Proceso getPropietario() {
        return propietario;
    }

    public void ocupar(Proceso proceso) {
        this.ocupado = true;
        this.propietario = proceso;
    }

    public void liberar() {
        this.ocupado = false;
        this.propietario = null;
    }

    
}