package client;

import chess.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import websocket.messages.*;
import ui.GameSocketClient;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class SocketTests {

    static final String SERVER_URL = "ws://localhost:8080/ws";
    static final String AUTH_TOKEN = "test-auth-token";  // Replace with a real one
    static final int GAME_ID = 1000;                      // Replace with a real game ID
    static GameSocketClient socketClient;
    static ChessGame.TeamColor COLOR = ChessGame.TeamColor.WHITE;

    static final CountDownLatch latch = new CountDownLatch(1);
    static volatile ServerMessage receivedMessage;

    @BeforeAll
    public static void setup() throws Exception {
        socketClient = new GameSocketClient(SERVER_URL, message -> {
            receivedMessage = message;
            latch.countDown();
        });
        socketClient.connect(AUTH_TOKEN, GAME_ID);
        Thread.sleep(1000); // give time for connection
    }

    @Test
    public void testConnectAndLoadGame() throws Exception {
        socketClient.connect(AUTH_TOKEN, GAME_ID);

        boolean success = latch.await(2, TimeUnit.SECONDS);
        assertTrue(success, "Should receive a message from server");

        assertNotNull(receivedMessage);
        assertEquals(ServerMessage.ServerMessageType.LOAD_GAME, receivedMessage.getServerMessageType());
        assertNotNull(((LoadGameMessage) receivedMessage).getGame());
    }

    @Test
    public void testSendMoveCommand() throws Exception {
        ChessPosition from = new ChessPosition(2, 5); // e2
        ChessPosition to = new ChessPosition(4, 5);   // e4
        ChessMove move = new ChessMove(from, to, null);

        socketClient.sendMove(AUTH_TOKEN, GAME_ID, COLOR, move);
        // You may need to wait for a LOAD_GAME or NOTIFICATION response
    }

    @Test
    public void testResignCommand() {
        socketClient.sendResign(AUTH_TOKEN, GAME_ID);
        // You may want to assert that the game is marked as over in DB manually
    }

    @Test
    public void testLeaveCommand() {
        socketClient.sendLeave(AUTH_TOKEN, GAME_ID);
        // Should trigger disconnect and server cleanup
    }
}
