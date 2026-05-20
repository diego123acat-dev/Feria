package com.example.scheduler;

import java.util.LinkedList;
import java.util.Queue;

import com.example.model.EstadoProceso;
import com.example.model.Proceso;

public class RoundRobinPlanificador implements Planificador {

    private final Queue<Proceso> readyQueue;
    private Proceso procesoActual;
    private final int quantum;
    private int contadorQuantum;

    public RoundRobinPlanificador(int quantum) {
        this.quantum = quantum;
        this.readyQueue = new LinkedList<>();
        this.contadorQuantum = 0;
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
        readyQueue.add(proceso);
    }

    @Override
    public void ejecutarTick() {
        if (procesoActual == null) {
            procesoActual = seleccionarProceso();
            contadorQuantum = 0;
        }

        if (procesoActual == null) {
            return;
        }

        procesoActual.ejecutar();
        contadorQuantum++;

        if (procesoActual.terminado()) {
            procesoActual = null;
            contadorQuantum = 0;
            return;
        }

        if (contadorQuantum >= quantum) {
            agregarProceso(procesoActual);
            procesoActual = null;
            contadorQuantum = 0;
        }
    }

    @Override
    public Proceso getProcesoActual() {
        return procesoActual;
    }
}
