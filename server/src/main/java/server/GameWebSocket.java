package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class GameWebSocket {

    private static final Gson gson = new Gson();
    private static WebSocketHandler handler;
    private static final Map<Session, Integer> sessionToGameID = new ConcurrentHashMap<>();

    public static void setHandler(WebSocketHandler h) {
        handler = h;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {}

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        int gameID = extractGameID(message);
        sessionToGameID.putIfAbsent(session, gameID);
        handler.joinGame(gameID, session);
        handler.receiveMessage(message, gameID, session);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        Integer gameID = sessionToGameID.get(session);
        if (gameID != null) {
            handler.leaveGame(gameID, session);
        }
    }

    private int extractGameID(String json) {
        String key = "\"gameID\":";
        int i = json.indexOf(key);
        if (i == -1) throw new IllegalArgumentException("Missing gameID");
        int start = i + key.length();
        int end = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("}", start);
        return Integer.parseInt(json.substring(start, end).trim());
    }
}
