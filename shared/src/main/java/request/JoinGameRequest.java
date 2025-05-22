package request;

public class JoinGameRequest {
    private String playerColor;
    private Integer gameID;

    public JoinGameRequest() {}

    public JoinGameRequest(String playerColor, Integer gameID) {
        this.playerColor = playerColor;
        this.gameID = gameID;
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public Integer getGameID() {
        return gameID;
    }
}
