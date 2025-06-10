package request;

import chess.ChessGame.TeamColor;
import chess.ChessMove;

public record MoveRequest(String authToken, int gameID, TeamColor playerColor, ChessMove move) {}
