package ui;

import com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerUtils {
    private static final Gson gson = new Gson();
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

    public static void sendRequestBody(HttpURLConnection connection, Object requestObj) throws IOException {
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = gson.toJson(requestObj).getBytes();
            os.write(input);
        }
    }

    public static <T> T readResponse(HttpURLConnection connection, Class<T> responseClass) throws IOException {
        InputStream stream = (connection.getResponseCode() >= 400)
                ? connection.getErrorStream()
                : connection.getInputStream();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            StringBuilder responseJson = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseJson.append(line);
            }
            return gson.fromJson(responseJson.toString(), responseClass);
        }
    }
}
