package com.example.scheduler;

import java.util.Queue;

import com.example.model.Proceso;

public class RoundRobinPlanificador implements Planificador {

    private final Queue<Proceso> readyQueue;
    private Proceso procesoActual;
    private final int quantum;
    private int contadorQuantum;

    public RoundRobinPlanificador(int quantum) {
        this.quantum = quantum;
        this.readyQueue = new java.util.LinkedList<>();
        this.contadorQuantum = 0;
    }

    @Override
    public Proceso seleccionarProceso() {
        return readyQueue.poll(); // Retorna el proceso seleccionado
    }

    @Override
    public void agregarProceso(Proceso proceso) {
        this.readyQueue.offer(proceso); // Agrega el proceso a la cola de listos
    }

    @Override
    public void ejecutarTick() {
        // 1. Si no hay proceso actual, tomar uno
        if (procesoActual == null) {
            procesoActual = readyQueue.poll();
            contadorQuantum = 0;
        }

        if (procesoActual == null){
            return;
        }

        // 2. Ejecutar 1 unidad de CPU
        procesoActual.ejecutar();
        contadorQuantum++;

        // 3. Si terminó el proceso
        if (procesoActual.terminado()) {
            procesoActual = null;
            return;
        }

        // 4. Si se acabó el quantum
        if (contadorQuantum >= quantum) {
            readyQueue.add(procesoActual); // vuelve al final
            procesoActual = null;
        }
    }

    @Override
    public Proceso getProcesoActual() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
