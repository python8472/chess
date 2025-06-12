package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.UserGameCommand;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class GameWebSocket {

    private static final Gson GSON = new Gson();
    private static WebSocketHandler handler;
    private static final Map<Session, Integer> SESSION_TO_GAME_ID  = new ConcurrentHashMap<>();

    public static void setHandler(WebSocketHandler h) {
        handler = h;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        // Optional: you could log the connection open if needed
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try {
            // Deserialize the message to extract game ID and auth token
            UserGameCommand command = GSON.fromJson(message, UserGameCommand.class);
            int gameID = command.getGameID();

            // Save session to game ID mapping if not already done
            SESSION_TO_GAME_ID .putIfAbsent(session, gameID);

            // Register session and handle the command
            handler.joinGame(gameID, session);
            handler.receiveMessage(message, gameID, session);

        } catch (Exception e) {
            e.printStackTrace();
            try {
                session.getRemote().sendString(
                        GSON.toJson(new websocket.messages.ErrorMessage("Error: Invalid message format."))
                );
            } catch (Exception ignored) {
            }
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        Integer gameID = SESSION_TO_GAME_ID .get(session);
        if (gameID != null) {
            handler.leaveGame(gameID, session);
        }
        SESSION_TO_GAME_ID .remove(session);
    }
}
