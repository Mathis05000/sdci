package com.example.monitor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Monitor {

    // URL de l'API Kubernetes
    private static final String URL_API = "https://192.168.10.101:6443/apis/custom.metrics.k8s.io/v1beta1/namespaces/default/pods/sdci-gwi/istio_requests_per_second";
    private static final String TOKEN = "eyJhbGciOiJSUzI1NiIsImtpZCI6InRzX0hFWkRtWWRxWE5SY1M4bzlFeXVMTWdBaXJ5Tl83Vi03MWRrZ1Z3VmMifQ.eyJhdWQiOlsiaHR0cHM6Ly9rdWJlcm5ldGVzLmRlZmF1bHQuc3ZjLmNsdXN0ZXIubG9jYWwiXSwiZXhwIjoxNzM4MjM4MzMzLCJpYXQiOjE3MzgyMzQ3MzMsImlzcyI6Imh0dHBzOi8va3ViZXJuZXRlcy5kZWZhdWx0LnN2Yy5jbHVzdGVyLmxvY2FsIiwia3ViZXJuZXRlcy5pbyI6eyJuYW1lc3BhY2UiOiJkZWZhdWx0Iiwic2VydmljZWFjY291bnQiOnsibmFtZSI6Im1ldHJpY3MtYWNjZXNzLXNhIiwidWlkIjoiMGNlZTYyZTMtOGI5Yy00NmU3LWIzYWItY2RjYTg3NjNkOTY4In19LCJuYmYiOjE3MzgyMzQ3MzMsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDpkZWZhdWx0Om1ldHJpY3MtYWNjZXNzLXNhIn0.esGLfjJzbBbOjpWrE1m-psYOK-Zan7X_LQZ04yr_L6qUe10igmXq9nuiNkzgom7N6Q5tVp_cU6sigKJdi-GOilhjBdqkxQmM2LIfn-4ZEQs2nyKaMNykLmYCr2tGyYclamBMUJGeeo3G3HFFekeGdM_uCl2L09LhUeDognJZupkUuxYFEnELbMkVDH2mYjFNHNRCG8a3HaSeNL7_UhWEMTnw6hAjORJGBXFge-NvidCznyt-3cBQ6jSMqX_quJZ1AgyhX-EUIxiZN9bjQCnM9ejrZc-TcDFVMI5RFXQicaPSYhphVPQIfj3YpWO_RDBy2zs7yvDs6rB0kZIT8Nnezg";
    private static final String API_URL = "https://192.168.10.101:6443/apis/apps/v1/namespaces/default/deployments/sdci-gwi/scale";

    private BlockingQueue<Double> dataQueue;

    public Monitor(BlockingQueue<Double> dataQueue) {
        this.dataQueue = dataQueue;
    }

    public void startMonitoring() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Tâche périodique qui s'exécute toutes les 5 secondes
        Runnable task = () -> {
            try {
                Double value = getData();
                int nbReplicas = getCurrentReplicas();
                Double rate = value / nbReplicas;
                if (value != null) {
                    // Envoyer la valeur récupérée dans la file
                    dataQueue.put(rate);
                    System.out.println("rate : " + rate);
                } else {
                    System.out.println("Erreur dans la récupération des données pour cette exécution.");
                }
            } catch (Exception e) {
                System.out.println("Erreur lors de l'exécution de la tâche : " + e.getMessage());
            }
        };

        // Lancer la tâche toutes les 5 secondes
        scheduler.scheduleAtFixedRate(task, 0, 5, TimeUnit.SECONDS);
    }

    // Récupérer les données depuis l'API Kubernetes
    private Double getData() throws Exception {
        String url = URL_API;
        String token = TOKEN;
        Double val = 0.;

        try {
            // Créer un objet URL à partir de l'URL de l'API
            URL apiUrl = new URL(url);

            // Ouvrir une connexion HTTP
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();

            // Configurer la méthode de la requête
            connection.setRequestMethod("GET");

            // Ajouter l'en-tête d'autorisation avec le token Bearer
            connection.setRequestProperty("Authorization", "Bearer " + token);

            // Lire la réponse de l'API
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());

            // Extraire le tableau "items"
            JSONArray items = jsonResponse.getJSONArray("items");

            // Extraire la première valeur du champ "value"
            String value = items.getJSONObject(0).getString("value");

            val = convertToDouble(value);
        } catch (Exception e) {
            throw new Exception("Erreur lors de la récupération des données", e);
        }

        return val;
    }

    private int getCurrentReplicas() throws Exception {
        // Faire une requête GET pour obtenir l'état actuel du Deployment
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + TOKEN);

        // Lire la réponse de l'API
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }
        reader.close();

        JSONObject responseJson = new JSONObject(response.toString());

        // Extraire le nombre de réplicas de la réponse JSON
        // En supposant que le nombre de réplicas se trouve sous "status" -> "replicas"
        int currentReplicas = responseJson.getJSONObject("status").getInt("replicas");

        System.out.println("replicas : " + currentReplicas);
        return currentReplicas;
    }

    public static double convertToDouble(String input) {
        // Vérifier si le format est avec "m"
        if (input.endsWith("m")) {
            // Enlever le "m" et convertir en double
            String numberStr = input.substring(0, input.length() - 1);
            try {
                // Convertir en double et diviser par 1000
                return Double.parseDouble(numberStr) / 1000.0;
            } catch (NumberFormatException e) {
                System.out.println("Format invalide pour " + input);
                return 0.0;  // Retourner une valeur par défaut en cas d'erreur
            }
        } else {
            // Si pas de "m", juste convertir la chaîne en double
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Format invalide pour " + input);
                return 0.0;  // Retourner une valeur par défaut en cas d'erreur
            }
        }
    }
}