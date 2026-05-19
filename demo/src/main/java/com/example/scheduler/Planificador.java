package com.example.scheduler;

import com.example.model.Proceso;

public interface Planificador {

    void agregarProceso(Proceso proceso);

    void ejecutarTick();

    Proceso getProcesoActual();

    Proceso seleccionarProceso();

}
