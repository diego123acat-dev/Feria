package com.example.scheduler;

import java.util.Queue;

import com.example.model.Proceso;

public class FCFSPlanificador implements Planificador {
    

    @Override
    public Proceso seleccionarProceso() {
        // Implementación del algoritmo FCFS (First-Come, First-Served)
        Queue<Proceso> colaListos = getColaListos(); // Método para obtener la cola de procesos listos
        if (colaListos.isEmpty()) {
            return null; // No hay procesos para ejecutar
        }
        return colaListos.poll(); // Retorna el proceso seleccionado
    }

    private Queue<Proceso> getColaListos() {
        // Implementación para obtener la cola de procesos listos

        // Esto puede ser una estructura de datos que mantenga los procesos en orden de llegada
        return null; // Placeholder, debe ser implementado según la estructura de datos utilizada
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
