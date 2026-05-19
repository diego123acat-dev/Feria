package com.example.scheduler;

import com.example.model.Proceso;

public class RoundRobinPlanificador implements Planificador {

    private final int quantum;

    public RoundRobinPlanificador(int quantum) {
        this.quantum = quantum;
    }

    @Override
    public Proceso seleccionarProceso() {
        // Implementación del algoritmo Round Robin
        int a=quantum;
        System.out.println("Quantum: "+a);
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
