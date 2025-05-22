package dataAccess;

import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();
    private int nextGameID = 1;

    @Override
    public List<GameData> listGames() {
        return new ArrayList<>(games.values());
    }

    @Override
    public void clear() {
        games.clear();
        nextGameID = 1;
    }

    @Override
    public int createGame(String gameName) {
        int gameID = nextGameID++;
        GameData game = new GameData(gameID, gameName, null, null);
        games.put(gameID, game);
        return gameID;
    }

}
