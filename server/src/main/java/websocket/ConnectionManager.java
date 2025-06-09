package websocket;

import java.util.*;

public class ConnectionManager {

    private final Map<Integer, Set<Connection>> gameConnections = new HashMap<>();

    // Singleton pattern
    private static final ConnectionManager instance = new ConnectionManager();

    public static ConnectionManager getInstance() {
        return instance;
    }

    private ConnectionManager() {}

    public void add(int gameID, Connection connection) {
        gameConnections.putIfAbsent(gameID, new HashSet<>());
        gameConnections.get(gameID).add(connection);
    }

    public void remove(int gameID, Connection connection) {
        if (gameConnections.containsKey(gameID)) {
            gameConnections.get(gameID).remove(connection);
            if (gameConnections.get(gameID).isEmpty()) {
                gameConnections.remove(gameID);
            }
        }
    }

    public void broadcast(int gameID, String messageJson) {
        Set<Connection> connections = gameConnections.getOrDefault(gameID, Set.of());
        for (Connection conn : connections) {
            conn.send(messageJson);
        }
    }
}
