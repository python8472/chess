package ui;

import com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerUtils {
    private static final Gson GSON  = new Gson();
    private static final String SERVER_URL = "http://localhost:8080"; // Change if needed

    public static HttpURLConnection makeRequest(String method, String path, String authToken) throws IOException {
        URL url = new URL(SERVER_URL + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", "application/json");

        if (authToken != null && !authToken.isBlank()) {
            connection.setRequestProperty("Authorization", authToken);
        }

        connection.setDoOutput(true);
        return connection;
    }

}
