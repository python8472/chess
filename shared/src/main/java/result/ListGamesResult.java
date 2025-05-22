package result;

import model.GameData;
import java.util.List;

public class ListGamesResult {
    private List<GameData> games;
    private String message;

    public ListGamesResult(List<GameData> games) {
        this.games = games;
    }

    public ListGamesResult(String message) {
        this.message = message;
    }

    public List<GameData> getGames() {
        return games;
    }

    public String getMessage() {
        return message;
    }
}
