package websocket.commands;

import chess.ChessGame.TeamColor;
import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {

    private final TeamColor playerColor;
    private final ChessMove move;

    public MakeMoveCommand(String authToken, int gameID, TeamColor playerColor, ChessMove move) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.playerColor = playerColor;
        this.move = move;
    }

    public TeamColor getPlayerColor() {
        return playerColor;
    }

    public ChessMove getMove() {
        return move;
    }
}
