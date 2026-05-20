package com.example.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.example.model.EstadoProceso;
import com.example.model.Proceso;
import com.example.scheduler.FCFSPlanificador;
import com.example.scheduler.Planificador;
import com.example.scheduler.RoundRobinPlanificador;
import com.example.scheduler.SJFPlanificador;
import com.example.util.GeneradorProcesos;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

public class SimuladorController {

    private static final double PROBABILIDAD_BLOQUEO = 0.0;
    private static final int BLOQUEO_MINIMO = 2;
    private static final int BLOQUEO_MAXIMO = 4;

    @FXML
    private ComboBox<String> comboAlgoritmo;
    @FXML
    private Spinner<Integer> spinnerQuantum;
    @FXML
    private Button btnIniciar;
    @FXML
    private Button btnPausar;
    @FXML
    private Button btnDetener;
    @FXML
    private Button btnAgregarProceso;
    @FXML
    private Button btnGenerar;
    @FXML
    private Label lblProcesoActual;
    @FXML
    private ProgressBar progressQuantum;
    @FXML
    private ListView<String> listReady;
    @FXML
    private ListView<String> listBlocked;
    @FXML
    private ListView<String> listFinished;
    @FXML
    private TableView<Proceso> tablaProcesos;
    @FXML
    private TableColumn<Proceso, Integer> colPID;
    @FXML
    private TableColumn<Proceso, String> colEstado;
    @FXML
    private TableColumn<Proceso, Integer> colTiempoTotal;
    @FXML
    private TableColumn<Proceso, Integer> colTiempoRestante;
    @FXML
    private TableColumn<Proceso, Integer> colLlegada;
    @FXML
    private GridPane gridGantt;
    @FXML
    private Label lblTiempo;
    @FXML
    private Label lblCPU;

    private final ObservableList<Proceso> procesos = FXCollections.observableArrayList();
    private final ObservableList<Proceso> readyQueue = FXCollections.observableArrayList();
    private final ObservableList<Proceso> blockedQueue = FXCollections.observableArrayList();
    private final ObservableList<Proceso> finishedQueue = FXCollections.observableArrayList();
    private final List<String> historialGantt = new ArrayList<>();
    private final Map<Proceso, Integer> tiemposBloqueo = new HashMap<>();
    private final Random random = new Random();

    private Timeline reloj;
    private Proceso procesoActual;
    private int tiempoSistema;
    private int quantumUsado;
    private int ticksCpuOcupada;

    @FXML
    @SuppressWarnings("unused")
    private void initialize() {
        configurarControles();
        configurarTabla();
        configurarEventos();
        crearReloj();
        actualizarVista();
    }

    private void configurarControles() {
        comboAlgoritmo.setItems(FXCollections.observableArrayList("Round Robin", "FCFS", "SJF"));
        comboAlgoritmo.getSelectionModel().select("Round Robin");

        spinnerQuantum.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 3));
        spinnerQuantum.setEditable(true);
    }

    private void configurarTabla() {
        tablaProcesos.setItems(procesos);
        colPID.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getId()));
        colEstado.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getEstado().name()));
        colLlegada.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getTiempoLlegada()));
        colTiempoTotal.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getTiempoEjecucion()));
        colTiempoRestante.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getTiempoRestante()));
    }

    private void configurarEventos() {
        btnIniciar.setOnAction(event -> iniciarSimulacion());
        btnPausar.setOnAction(event -> pausarSimulacion());
        btnDetener.setOnAction(event -> detenerSimulacion());
        btnAgregarProceso.setOnAction(event -> agregarProceso());
        btnGenerar.setOnAction(event -> generarProcesos(10));

        comboAlgoritmo.setOnAction(event -> {
            if (procesoActual == null) {
                ordenarReadyQueue();
                actualizarVista();
            }
        });
    }

    private void crearReloj() {
        reloj = new Timeline(new KeyFrame(Duration.seconds(1), event -> ejecutarTick()));
        reloj.setCycleCount(Timeline.INDEFINITE);
    }

    private void iniciarSimulacion() {
        if (procesos.isEmpty()) {
            generarProcesos(5);
        }
        reloj.play();
    }

    private void pausarSimulacion() {
        reloj.pause();
    }

    private void detenerSimulacion() {
        reloj.stop();
        procesos.clear();
        readyQueue.clear();
        blockedQueue.clear();
        finishedQueue.clear();
        historialGantt.clear();
        tiemposBloqueo.clear();
        procesoActual = null;
        tiempoSistema = 0;
        quantumUsado = 0;
        ticksCpuOcupada = 0;
        actualizarVista();
    }

    private void generarProcesos(int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            agregarProceso();
        }
    }

    private void agregarProceso() {
        Proceso proceso = GeneradorProcesos.generarProceso();
        procesos.add(proceso);

        if (proceso.getTiempoLlegada() <= tiempoSistema) {
            moverAReady(proceso);
        }

        ordenarReadyQueue();
        actualizarVista();
    }

    private void ejecutarTick() {
        tiempoSistema++;
        actualizarProcesosBloqueados();
        admitirProcesosNuevos();

        if (procesoActual == null) {
            procesoActual = seleccionarSiguienteProceso();
            quantumUsado = 0;
        }

        if (procesoActual != null) {
            historialGantt.add("P" + procesoActual.getId());
            ticksCpuOcupada++;
            procesoActual.ejecutar();
            quantumUsado++;

            if (procesoActual.terminado()) {
                finishedQueue.add(procesoActual);
                procesoActual = null;
                quantumUsado = 0;
            } else if (debeBloquearse()) {
                bloquearProcesoActual();
            } else if (esRoundRobin() && quantumUsado >= getQuantum()) {
                reencolarProcesoActual();
                procesoActual = null;
                quantumUsado = 0;
            }
        } else {
            historialGantt.add("IDLE");
        }

        if (procesoActual == null && readyQueue.isEmpty() && noHayProcesosPendientes()) {
            reloj.pause();
        }

        ordenarReadyQueue();
        actualizarVista();
    }

    private void actualizarProcesosBloqueados() {
        for (Proceso proceso : new ArrayList<>(blockedQueue)) {
            int ticksRestantes = tiemposBloqueo.getOrDefault(proceso, 1) - 1;

            if (ticksRestantes <= 0) {
                tiemposBloqueo.remove(proceso);
                blockedQueue.remove(proceso);
                moverAReady(proceso);
            } else {
                tiemposBloqueo.put(proceso, ticksRestantes);
            }
        }
    }

    private void admitirProcesosNuevos() {
        for (Proceso proceso : procesos) {
            if (proceso.getEstado() == EstadoProceso.NUEVO && proceso.getTiempoLlegada() <= tiempoSistema) {
                moverAReady(proceso);
            }
        }
    }

    private Proceso seleccionarSiguienteProceso() {
        if (readyQueue.isEmpty()) {
            return null;
        }

        Planificador planificador = crearPlanificadorActual();
        for (Proceso proceso : readyQueue) {
            planificador.agregarProceso(proceso);
        }

        Proceso siguiente = planificador.seleccionarProceso();
        readyQueue.remove(siguiente);
        return siguiente;
    }

    private Planificador crearPlanificadorActual() {
        String algoritmo = comboAlgoritmo.getValue();

        if ("FCFS".equals(algoritmo)) {
            return new FCFSPlanificador();
        }

        if ("SJF".equals(algoritmo)) {
            return new SJFPlanificador();
        }

        return new RoundRobinPlanificador(getQuantum());
    }

    private void moverAReady(Proceso proceso) {
        if (!readyQueue.contains(proceso)
                && !blockedQueue.contains(proceso)
                && !finishedQueue.contains(proceso)
                && proceso != procesoActual) {
            proceso.setEstado(EstadoProceso.READY);
            readyQueue.add(proceso);
        }
    }

    private void reencolarProcesoActual() {
        if (procesoActual == null
                || procesoActual.terminado()
                || readyQueue.contains(procesoActual)
                || blockedQueue.contains(procesoActual)
                || finishedQueue.contains(procesoActual)) {
            return;
        }

        procesoActual.setEstado(EstadoProceso.READY);
        readyQueue.add(procesoActual);
    }

    private boolean debeBloquearse() {
        return random.nextDouble() < PROBABILIDAD_BLOQUEO;
    }

    private void bloquearProcesoActual() {
        procesoActual.setEstado(EstadoProceso.BLOCKED);
        blockedQueue.add(procesoActual);
        tiemposBloqueo.put(procesoActual, generarTiempoBloqueo());
        procesoActual = null;
        quantumUsado = 0;
    }

    private int generarTiempoBloqueo() {
        return random.nextInt(BLOQUEO_MAXIMO - BLOQUEO_MINIMO + 1) + BLOQUEO_MINIMO;
    }

    private void ordenarReadyQueue() {
        if ("SJF".equals(comboAlgoritmo.getValue())) {
            FXCollections.sort(readyQueue,
                    (p1, p2) -> Integer.compare(p1.getTiempoRestante(), p2.getTiempoRestante()));
        }
    }

    private boolean noHayProcesosPendientes() {
        for (Proceso proceso : procesos) {
            if (proceso.getEstado() != EstadoProceso.TERMINATED) {
                return false;
            }
        }
        return true;
    }

    private boolean esRoundRobin() {
        return "Round Robin".equals(comboAlgoritmo.getValue());
    }

    private int getQuantum() {
        return spinnerQuantum.getValue();
    }

    private void actualizarVista() {
        lblProcesoActual.setText(procesoActual == null ? "IDLE" : "P" + procesoActual.getId());
        progressQuantum.setProgress(calcularProgresoQuantum());

        listReady.setItems(convertirProcesos(readyQueue));
        listBlocked.setItems(convertirProcesos(blockedQueue));
        listFinished.setItems(convertirProcesos(finishedQueue));

        lblTiempo.setText("Tiempo: " + tiempoSistema);
        lblCPU.setText("CPU: " + calcularUsoCpu() + "%");

        tablaProcesos.refresh();
        actualizarGantt();
    }

    private ObservableList<String> convertirProcesos(ObservableList<Proceso> cola) {
        ObservableList<String> resultado = FXCollections.observableArrayList();
        for (Proceso proceso : cola) {
            resultado.add(formatearProceso(proceso));
        }
        return resultado;
    }

    private String formatearProceso(Proceso proceso) {
        String texto = "P" + proceso.getId()
                + " | Restante: " + proceso.getTiempoRestante()
                + " | Llegada: " + proceso.getTiempoLlegada();

        if (proceso.getEstado() == EstadoProceso.BLOCKED) {
            texto += " | Bloqueo: " + tiemposBloqueo.getOrDefault(proceso, 0);
        }

        return texto;
    }

    private double calcularProgresoQuantum() {
        if (!esRoundRobin() || procesoActual == null) {
            return 0.0;
        }
        return (double) quantumUsado / getQuantum();
    }

    private int calcularUsoCpu() {
        if (tiempoSistema == 0) {
            return 0;
        }
        return (int) Math.round((ticksCpuOcupada * 100.0) / tiempoSistema);
    }

    private void actualizarGantt() {
        gridGantt.getChildren().clear();

        agregarCeldaGantt("", 0, 0, 72, 24, "#FFFFFF", "#111827", false);

        for (int tiempo = 0; tiempo < historialGantt.size(); tiempo++) {
            agregarCeldaGantt(String.valueOf(tiempo + 1), tiempo + 1, 0, 34, 24,
                    "#FFFFFF", "#4B5563", true);
        }

        int fila = 1;
        for (Proceso proceso : procesos) {
            String pid = "P" + proceso.getId();
            agregarCeldaGantt(pid, 0, fila, 72, 34, "#E5E7EB", "#374151", true);

            for (int tiempo = 0; tiempo < historialGantt.size(); tiempo++) {
                boolean ejecutado = pid.equals(historialGantt.get(tiempo));
                agregarCeldaGantt(ejecutado ? pid : "", tiempo + 1, fila, 34, 34,
                        ejecutado ? obtenerColorProceso(pid) : "#E5E7EB",
                        ejecutado ? "#FFFFFF" : "#E5E7EB",
                        ejecutado);
            }

            fila++;
        }

        if (historialGantt.contains("IDLE")) {
            agregarCeldaGantt("IDLE", 0, fila, 72, 34, "#E5E7EB", "#374151", true);

            for (int tiempo = 0; tiempo < historialGantt.size(); tiempo++) {
                boolean idle = "IDLE".equals(historialGantt.get(tiempo));
                agregarCeldaGantt(idle ? "IDLE" : "", tiempo + 1, fila, 34, 34,
                        idle ? obtenerColorProceso("IDLE") : "#E5E7EB",
                        idle ? "#FFFFFF" : "#E5E7EB",
                        idle);
            }
        }
    }

    private void agregarCeldaGantt(String texto, int columna, int fila, int ancho, int alto,
            String fondo, String colorTexto, boolean negrita) {
        Label celda = new Label(texto);
        celda.setMinSize(ancho, alto);
        celda.setPrefSize(ancho, alto);
        celda.setMaxSize(ancho, alto);
        celda.setStyle("-fx-background-color: " + fondo + ";"
                + "-fx-text-fill: " + colorTexto + ";"
                + "-fx-font-size: 11;"
                + "-fx-font-weight: " + (negrita ? "bold" : "normal") + ";"
                + "-fx-alignment: center;"
                + "-fx-background-radius: 5;");

        gridGantt.add(celda, columna, fila);
    }

    private String obtenerColorProceso(String proceso) {
        if ("IDLE".equals(proceso)) {
            return "#475569";
        }

        int numeroProceso = Integer.parseInt(proceso.substring(1));
        String[] colores = {
                "#3B82F6",
                "#10B981",
                "#F59E0B",
                "#EF4444",
                "#8B5CF6",
                "#14B8A6",
                "#F97316",
                "#EC4899"
        };
        return colores[numeroProceso % colores.length];
    }
}
