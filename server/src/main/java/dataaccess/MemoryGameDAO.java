package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MemoryGameDAO implements GameDAO {
    private final HashMap<Integer, GameData> games = new HashMap<>();
    private final AtomicInteger gameIDCounter = new AtomicInteger(1);

    @Override
    public int createGame(String gameName) {
        int id = gameIDCounter.getAndIncrement();
        GameData game = new GameData(id, gameName, null, null, null);
        games.put(id, game);
        return id;
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public void updateGame( GameData game) {
        games.put(game.getGameID(), game);
    }

    @Override
    public List<GameData> listGames() {
        return new ArrayList<>(games.values());
    }

    @Override
    public void clear() {
        games.clear();
    }
}
