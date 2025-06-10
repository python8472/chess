package websocket.messages;

import chess.ChessGame;

/**
 * Message sent to synchronize a client with the full game state.
 */
public class LoadGameMessage extends ServerMessage {

    private final ChessGame game;

    public LoadGameMessage(ChessGame game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public ChessGame getGame() {
        return game;
    }
}
