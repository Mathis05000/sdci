package com.example.Planner;

import java.util.concurrent.BlockingQueue;

public class Planner {

    private BlockingQueue<Double> dataQueueIn;
    private BlockingQueue<String> dataQueueOut;

    private String SCALE_UP_PLAN = "SCALE_UP_PLAN";
    private String SCALE_DOWN_PLAN = "SCALE_DOWN_PLAN";

    private long lastPlanTime = 0;  // Pour stocker le temps de la dernière exécution du plan
    private static final long WAIT_TIME = 30000;

    public Planner(BlockingQueue<Double> dataQueueIn, BlockingQueue<String> dataQueueOut) {
        this.dataQueueIn = dataQueueIn;
        this.dataQueueOut = dataQueueOut;
    }

    public void startPlanning() {
        // Tâche de planification qui attend indéfiniment une valeur de la file
        Runnable task = () -> {
            while (true) {
                try {
                    // Attendre la valeur du Planner (cette méthode bloque le thread jusqu'à ce qu'une valeur soit disponible)
                    Double value = dataQueueIn.take();
                    // Planifier l'action en fonction de la valeur reçue
                    planAction(value);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        // Exécuter le thread de planification
        new Thread(task).start();
    }

    private void planAction(Double value) {
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPlanTime >= WAIT_TIME) {
            if (value > 1) {
                try {
                    dataQueueOut.put(SCALE_UP_PLAN);
                    System.err.println("scale up");
                    lastPlanTime = currentTime;
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (value < 0.5) {
                try {
                    dataQueueOut.put(SCALE_DOWN_PLAN);
                    System.err.println("scale down");
                    lastPlanTime = currentTime;
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
            } 
        }
        
    }
}
