package ui;

import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessGame;
import com.google.gson.Gson;
import websocket.commands.*;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.function.Consumer;

@ClientEndpoint
public class GameSocketClient {

    private final String serverUrl;
    private final Consumer<ServerMessage> messageHandler;
    private Session session;
    private final Gson gson = new Gson();

    public GameSocketClient(String serverUrl, Consumer<ServerMessage> messageHandler) {
        this.serverUrl = serverUrl;
        this.messageHandler = messageHandler;
    }

    public void connect(String authToken, int gameID) throws Exception {
        String fullUri = serverUrl + "?authToken=" + authToken + "&gameID=" + gameID;
        System.out.println("[DEBUG] Connecting to WebSocket URI: " + fullUri);

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        try {
            container.connectToServer(this, URI.create(fullUri));
            System.out.println("[DEBUG] WebSocket container connected successfully.");
        } catch (Exception e) {
            System.err.println("[ERROR] WebSocket handshake failed:");
            e.printStackTrace();
            throw e;
        }

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

    public void sendHighlight(String authToken, int gameID, ChessPosition position) {
        sendCommand(new HighlightMovesCommand(authToken, gameID, position));
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
        System.out.println("[WebSocket] Connected to server.");
    }

    @OnMessage
    public void onMessage(String messageJson) {
        ServerMessage message = gson.fromJson(messageJson, ServerMessage.class);
        messageHandler.accept(message);
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
