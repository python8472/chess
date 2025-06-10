package websocket.commands;

import chess.ChessPosition;

public class HighlightMovesCommand extends UserGameCommand {

    private final ChessPosition position;

    public HighlightMovesCommand(String authToken, int gameID, ChessPosition position) {
        super(CommandType.MAKE_MOVE, authToken, gameID); // Or define a new CommandType if needed
        this.position = position;
    }

    public ChessPosition getPosition() {
        return position;
    }
}
