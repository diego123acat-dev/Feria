package com.example.scheduler;

import java.util.PriorityQueue;

import com.example.model.EstadoProceso;
import com.example.model.Proceso;

public class SJFPlanificador implements Planificador {

    private final PriorityQueue<Proceso> readyQueue;
    private Proceso procesoActual;

    public SJFPlanificador() {
        this.readyQueue = new PriorityQueue<>((p1, p2) -> {
            int comparacionTiempo = Integer.compare(p1.getTiempoRestante(), p2.getTiempoRestante());
            if (comparacionTiempo != 0) {
                return comparacionTiempo;
            }
            return Integer.compare(p1.getId(), p2.getId());
        });
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
