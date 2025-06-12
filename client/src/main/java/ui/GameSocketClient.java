package ui;

import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessGame;
import com.google.gson.Gson;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.function.Consumer;

@ClientEndpoint
public class GameSocketClient {

    private final String serverUrl;
    private Session session;
    private final Gson gson = new Gson();
    private final Consumer<ServerMessage> handler;

    public GameSocketClient(String serverUrl, Consumer<ServerMessage> handler) {
        this.serverUrl = serverUrl;
        this.handler = handler;
    }

    public void connect(String authToken, int gameID) throws Exception {
        String fullUri = serverUrl + "?authToken=" + authToken + "&gameID=" + gameID;

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        try {
            container.connectToServer(this, URI.create(fullUri));
        } catch (Exception e) {
            System.err.println("[ERROR] WebSocket handshake failed:");
            throw e;
        }

        sendCommand(new ConnectCommand(authToken, gameID));

        // Optional: wait for @OnOpen to confirm
    }

    public void sendMove(String authToken, int gameID, ChessGame.TeamColor color, ChessMove move) {
        sendCommand(new MakeMoveCommand(authToken, gameID, color, move));
    }

    public void sendResign(String authToken, int gameID) {
        sendCommand(new ResignCommand(authToken, gameID));
    }

    public void sendLeave(String authToken, int gameID) {
        sendCommand(new LeaveCommand(authToken, gameID));
    }

    public void sendHighlight(String authToken, int gameID, ChessPosition pos) {
        HighlightMovesCommand command = new HighlightMovesCommand(authToken, gameID, pos);
        String json = gson.toJson(command);
        session.getAsyncRemote().sendText(json);
        System.out.println("[DEBUG] HighlightMovesCommand JSON: " + json);
    }


    private void sendCommand(UserGameCommand command) {
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(gson.toJson(command));
            } catch (IOException e) {
                System.err.println("WebSocket send failed: " + e.getMessage());
            }
        } else {
            System.err.println("WebSocket not connected.");
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;

    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            ServerMessage base = gson.fromJson(message, ServerMessage.class);
            switch (base.getServerMessageType()) {
                case LOAD_GAME -> handler.accept(gson.fromJson(message, LoadGameMessage.class));
                case NOTIFICATION -> handler.accept(gson.fromJson(message, NotificationMessage.class));
                case ERROR -> handler.accept(gson.fromJson(message, ErrorMessage.class));
            }
        } catch (Exception e) {
            System.out.println("[WebSocket] Message handling error: " + e.getMessage());
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("[WebSocket] Connection closed: " + reason);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("[WebSocket] Error: " + throwable.getMessage());
    }
}
