package com.example.scheduler;

import java.util.PriorityQueue;

import com.example.model.Proceso;

public class SJFPlanificador implements Planificador {

    private final PriorityQueue<Proceso> readyQueue;
    private Proceso procesoActual;

    public SJFPlanificador() {
        this.readyQueue = new PriorityQueue<>((p1, p2) -> {
            if (p1.getTiempoEjecucion() != p2.getTiempoEjecucion()) {
                return Integer.compare(p1.getTiempoEjecucion(), p2.getTiempoEjecucion());
            }
            return Integer.compare(p1.getId(), p2.getId());
        });
        this.procesoActual = null;
    }

    @Override
    public Proceso seleccionarProceso() {
        return readyQueue.poll();
    }

    @Override
    public void agregarProceso(Proceso proceso) {
        this.readyQueue.offer(proceso);
    }

    @Override
    public void ejecutarTick() {
        this.procesoActual = seleccionarProceso(); // Selecciona el proceso a ejecutar
        if (procesoActual != null) {
            procesoActual.ejecutar(); // Simula la ejecución del proceso actual
            if (procesoActual.terminado()) {
                procesoActual = null; // El proceso ha terminado
            }
        }
    }

    @Override
    public Proceso getProcesoActual() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
