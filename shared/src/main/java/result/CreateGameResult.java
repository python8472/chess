package result;

public class CreateGameResult {
    private Integer gameID;
    private String message;

    public CreateGameResult(Integer gameID) {
        this.gameID = gameID;
    }

    public CreateGameResult(String message) {
        this.message = message;
    }

    public Integer getGameID() {
        return gameID;
    }

    public String getMessage() {
        return message;
    }
}
