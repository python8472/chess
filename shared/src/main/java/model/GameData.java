package model;

import chess.ChessGame;

/**
 * Represents a game on the server.
 */
public class GameData {
    private final int gameID;
    private final String gameName;
    private final String whiteUsername;
    private final String blackUsername;
    private final ChessGame game;  // For serializing

    public GameData(int gameID, String gameName, String whiteUsername, String blackUsername, ChessGame game) {
        this.gameID = gameID;
        this.gameName = gameName;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.game = game;
    }

    public int getGameID() {
        return gameID;
    }

    public String getGameName() {
        return gameName;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public ChessGame game() {return game;}
}
