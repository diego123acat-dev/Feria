package com.example.scheduler;

import com.example.model.Proceso;

public class SJFPlanificador implements Planificador {

    @Override
    public Proceso seleccionarProceso() {
        // Implementación del algoritmo SJF (Shortest Job First)
        return null; // Retorna el proceso seleccionado
    }

    @Override
    public void agregarProceso(Proceso proceso) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void ejecutarTick() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Proceso getProcesoActual() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
