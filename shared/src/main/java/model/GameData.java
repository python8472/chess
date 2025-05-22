package model;

/**
 * Represents a game on the server.
 */
public class GameData {
    private final int gameID;
    private final String gameName;
    private final String whiteUsername;
    private final String blackUsername;

    public GameData(int gameID, String gameName, String whiteUsername, String blackUsername) {
        this.gameID = gameID;
        this.gameName = gameName;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
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
}
