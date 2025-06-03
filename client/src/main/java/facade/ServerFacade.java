package facade;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import request.*;
import result.*;

public class ServerFacade {
    private final String serverUrl;
    private final Gson gson = new Gson();

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    // Them API Methods

    public RegisterResult register(RegisterRequest request) throws IOException {
        return makeRequest("/user", request, RegisterResult.class);
    }

    public LoginResult login(LoginRequest request) throws IOException {
        return makeRequest("/session", request, LoginResult.class);
    }

    public LogoutResult logout(LogoutRequest request, String authToken) throws IOException {
        return makeRequestWithAuth("DELETE", "/session", request, LogoutResult.class, authToken);
    }

    public ListGamesResult listGames(String authToken) throws IOException {
        return makeRequestWithAuth("GET", "/game", null, ListGamesResult.class, authToken);
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws IOException {
        return makeRequestWithAuth("POST", "/game", request, CreateGameResult.class, authToken);
    }

    public JoinGameResult joinGame(JoinGameRequest request, String authToken) throws IOException {
        return makeRequestWithAuth("PUT", "/game", request, JoinGameResult.class, authToken);
    }

    // HTTP Methods for helping

    private <T> T makeRequest(String path, Object request, Class<T> responseClass) throws IOException {
        URI uri = URI.create(serverUrl + path);
        URL url = uri.toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        if (request != null) {
            writeRequest(connection, request);
        }

        return readResult(connection, responseClass);
    }

    private <T> T makeRequestWithAuth(String method, String path, Object request, Class<T> responseClass, String authToken) throws IOException {
        URI uri = URI.create(serverUrl + path);
        URL url = uri.toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", authToken);

        if (request != null) {
            writeRequest(connection, request);
        }

        return readResult(connection, responseClass);
    }

    private void writeRequest(HttpURLConnection connection, Object request) throws IOException {
        String json = gson.toJson(request);
        try (OutputStream os = connection.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }
    }

    private <T> T readResult(HttpURLConnection connection, Class<T> responseClass) throws IOException {
        int status = connection.getResponseCode();
        InputStream responseStream = (status >= 200 && status < 300)
                ? connection.getInputStream()
                : connection.getErrorStream();

        try (InputStreamReader reader = new InputStreamReader(responseStream)) {
            return gson.fromJson(reader, responseClass);
        }
    }
}
