package com.example;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.example.Planner.Planner;
import com.example.analyser.Analyzer;
import com.example.executor.Executor;
import com.example.monitor.Monitor;

public class App {
    public static void main(String[] args) {
        // Créer une file partagée pour communiquer entre Monitor et Analyzer
        BlockingQueue<Double> dataQueue1 = new LinkedBlockingQueue<>();
        BlockingQueue<Double> dataQueue2 = new LinkedBlockingQueue<>();
        BlockingQueue<String> dataQueue3 = new LinkedBlockingQueue<>();

        // Créer une instance de Monitor et démarrer la surveillance
        Monitor monitor = new Monitor(dataQueue1);
        monitor.startMonitoring();

        // Créer une instance d'Analyzer et démarrer l'analyse
        Analyzer analyzer = new Analyzer(dataQueue1, dataQueue2);
        analyzer.startAnalysis();

        // Créer une instance de Planner et démarrer la plannification
        Planner planner = new Planner(dataQueue2, dataQueue3);
        planner.startPlanning();

        // Créer une instance de Executor et démarrer l'execution
        Executor executor = new Executor(dataQueue3);
        executor.startExecuting();



        // Vous pouvez ajouter d'autres tâches dans le main si nécessaire
        System.out.println("Surveillance et analyse en cours...");
    }
}
