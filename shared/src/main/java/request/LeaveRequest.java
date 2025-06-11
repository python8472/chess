package request;

public class LeaveRequest {
    private final String authToken;
    private final int gameID;

    public LeaveRequest(String authToken, int gameID) {
        this.authToken = authToken;
        this.gameID = gameID;
    }

    public String getAuthToken() {
        return authToken;
    }

    public int getGameID() {
        return gameID;
    }
}
