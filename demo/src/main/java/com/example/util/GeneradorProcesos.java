package com.example.util;

import com.example.model.Proceso;

public class GeneradorProcesos {
    private static int idCounter = 1;
    public static Proceso generarProceso() {
        int id = idCounter++;
        int tiempoEjecucion = (int) (Math.random() * 10) + 1; // Tiempo de ejecución entre 1 y 10
        int tiempoLlegada = (int) (Math.random() * 10); // Tiempo de llegada entre 0 y 9
        return new Proceso(id, tiempoLlegada, tiempoEjecucion);
    }
}
