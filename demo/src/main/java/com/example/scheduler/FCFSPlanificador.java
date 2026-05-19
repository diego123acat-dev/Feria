package com.example.scheduler;

import java.util.LinkedList;
import java.util.Queue;

import com.example.model.EstadoProceso;
import com.example.model.Proceso;

public class FCFSPlanificador implements Planificador {

    private final Queue<Proceso> readyQueue;
    private Proceso procesoActual;

    public FCFSPlanificador() {
        readyQueue = new LinkedList<>();
    }

    @Override
    public Proceso seleccionarProceso() {
        Proceso proceso = readyQueue.poll();
        if (proceso != null) {
            proceso.setEstado(EstadoProceso.RUNNING);
        }
        return proceso;
    }

    @Override
    public void agregarProceso(Proceso proceso) {
        proceso.setEstado(EstadoProceso.READY);
        readyQueue.offer(proceso);
    }

    @Override
    public void ejecutarTick() {
        if (procesoActual == null) {
            procesoActual = seleccionarProceso();
        }

        if (procesoActual == null) {
            return;
        }

        procesoActual.ejecutar();
        if (procesoActual.terminado()) {
            procesoActual = null;
        }
    }

    @Override
    public Proceso getProcesoActual() {
        return procesoActual;
    }
}
