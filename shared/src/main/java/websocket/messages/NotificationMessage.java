package websocket.messages;

/**
 * Message sent to all clients when a player performs an action (move, resign, etc.)
 */
public class NotificationMessage extends ServerMessage {
    private final String message;

    public NotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

