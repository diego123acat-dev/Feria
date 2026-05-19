package com.example.scheduler;

import java.util.LinkedList;
import java.util.Queue;

import com.example.model.Proceso;

public class FCFSPlanificador implements Planificador {
    
    private final Queue<Proceso> readyQueue;
    private Proceso procesoActual;

    public FCFSPlanificador() {
        readyQueue = new LinkedList<>();
    }

    @Override
    public Proceso seleccionarProceso() {
        if (readyQueue.isEmpty()) {
            return null;
        }
        return readyQueue.poll();
    }

    @SuppressWarnings("unused")
    private Queue<Proceso> getColaListos() {
        // Implementación para obtener la cola de procesos listos
        return readyQueue;
    }

    @Override
    public void agregarProceso(Proceso proceso) {
        readyQueue.add(proceso); // Agrega el proceso a la cola de listos
    }

    @Override
    public void ejecutarTick() {
        this.procesoActual = seleccionarProceso(); // Selecciona el proceso a ejecutar
        while (procesoActual != null) {
            procesoActual.ejecutar(); // Simula la ejecución del proceso actual
            if (procesoActual.terminado()) {
                procesoActual = null; // El proceso ha terminado
            }
        }
    }

    @Override
    public Proceso getProcesoActual() {
        return procesoActual;
    }

}
