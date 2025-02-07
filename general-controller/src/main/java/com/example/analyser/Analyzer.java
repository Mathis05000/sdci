package com.example.analyser;

import java.util.concurrent.BlockingQueue;

public class Analyzer {

    private BlockingQueue<Double> dataQueueIn;
    private BlockingQueue<Double> dataQueueOut;

    public Analyzer(BlockingQueue<Double> dataQueueIn, BlockingQueue<Double> dataQueueOut) {
        this.dataQueueIn = dataQueueIn;
        this.dataQueueOut = dataQueueOut;
    }

    public void startAnalysis() {
        Runnable task = () -> {
            while (true) {
                try {
                    // Attendre une valeur de la file
                    Double value = dataQueueIn.take();
                    // Analyser la valeur
                    analyze(value);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        // Exécuter le thread d'analyse
        new Thread(task).start();
    }

    private void analyze(Double value) {
        
        if (value > 1) {
            try {
                // Envoyer la valeur récupérée dans la file
                dataQueueOut.put(value);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (value < 0.5) {
            try {
                // Envoyer la valeur récupérée dans la file
                dataQueueOut.put(value);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
