package websocket.commands;

import chess.ChessPosition;

public class HighlightMovesCommand extends UserGameCommand {
    private final ChessPosition position;

    public HighlightMovesCommand(String authToken, int gameID, ChessPosition position) {
        super(CommandType.HIGHLIGHT, authToken, gameID);
        this.position = position;
    }


    public ChessPosition getPosition() {
        return position;
    }
}
