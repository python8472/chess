package passoff.server;

import com.google.gson.Gson;
import passoff.model.*;
import request.*;


import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class TestServerFacade_test {
    private final String baseUrl;
    private final Gson gson = new Gson();
    private int statusCode;

    public TestServerFacade_test(String host, String port) {
        this.baseUrl = "http://" + host + ":" + port;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String file(String urlPath) {
        try {
            HttpURLConnection connection = connect("GET", urlPath, null);
            connection.connect();

            statusCode = connection.getResponseCode();
            return readBody(connection);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public TestAuthResult register(TestUser user) {
        return this.send("/user", "POST", user, TestAuthResult.class, null);
    }

    public TestAuthResult login(TestUser user) {
        LoginRequest request = new LoginRequest(user.getUsername(), user.getPassword());
        return this.send("/session", "POST", request, TestAuthResult.class, null);
    }

    public TestResult logout(String authToken) {
        return this.send("/session", "DELETE", null, TestResult.class, authToken);
    }

    public TestCreateResult createGame(TestCreateRequest request, String authToken) {
        return this.send("/game", "POST", request, TestCreateResult.class, authToken);
    }

    public TestResult joinPlayer(TestJoinRequest request, String authToken) {
        return this.send("/game", "PUT", request, TestResult.class, authToken);
    }

    public TestListResult listGames(String authToken) {
        return this.send("/game", "GET", null, TestListResult.class, authToken);
    }

    public TestResult clear() {
        return this.send("/db", "DELETE", null, TestResult.class, null);
    }

    // --- Core Helper Method ---

    private <T> T send(String urlPath, String method, Object body, Class<T> resultClass, String authToken) {
        try {
            HttpURLConnection connection = connect(method, urlPath, authToken);
            connection.connect();

            if (body != null) {
                writeBody(connection, gson.toJson(body));
            }

            statusCode = connection.getResponseCode();
            String response = readBody(connection);
            return gson.fromJson(response, resultClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private HttpURLConnection connect(String method, String urlPath, String authToken) throws IOException {
        URL url = URI.create(baseUrl + urlPath).toURL();  // updated to avoid deprecation
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.addRequestProperty("Accept", "application/json");
        if (authToken != null) {
            connection.addRequestProperty("Authorization", authToken);
        }
        if (!method.equals("GET") && !method.equals("DELETE")) {
            connection.addRequestProperty("Content-Type", "application/json");
        }
        return connection;
    }

    private void writeBody(HttpURLConnection connection, String jsonBody) throws IOException {
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
    }

    private String readBody(HttpURLConnection connection) throws IOException {
        InputStream is = (statusCode < HttpURLConnection.HTTP_BAD_REQUEST)
                ? connection.getInputStream()
                : connection.getErrorStream();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                body.append(line);
            }
            return body.toString();
        }
    }

    // Optional for unit testing
    public String buildUrl(String endpoint) {
        return baseUrl + endpoint;
    }

    public String serialize(Object o) {
        return gson.toJson(o);
    }
}
