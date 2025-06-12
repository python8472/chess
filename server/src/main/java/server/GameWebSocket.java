package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.UserGameCommand;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class GameWebSocket {

    private static final Gson gSon = new Gson();
    private static WebSocketHandler handler;
    private static final Map<Session, Integer> sessionToGameID = new ConcurrentHashMap<>();

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
            UserGameCommand command = gSon.fromJson(message, UserGameCommand.class);
            int gameID = command.getGameID();

            // Save session to game ID mapping if not already done
            sessionToGameID.putIfAbsent(session, gameID);

            // Register session and handle the command
            handler.joinGame(gameID, session);
            handler.receiveMessage(message, gameID, session);

        } catch (Exception e) {
            e.printStackTrace();
            try {
                session.getRemote().sendString(
                        gSon.toJson(new websocket.messages.ErrorMessage("Error: Invalid message format."))
                );
            } catch (Exception ignored) {
            }
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        Integer gameID = sessionToGameID.get(session);
        if (gameID != null) {
            handler.leaveGame(gameID, session);
        }
        sessionToGameID.remove(session);
    }
}
