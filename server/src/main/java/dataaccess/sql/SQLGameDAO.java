package dataaccess.sql;

import com.google.gson.Gson;
import dataaccess.GameDAO;
import model.GameData;

import java.util.List;
import java.util.ArrayList;

public class SQLGameDAO implements GameDAO {

    private final Gson gson = new Gson();

    @Override
    public int createGame(String gameName) {
        // TODO: INSERT INTO games (gameName, game) VALUES (?, ?)
        return 0;
    }

    @Override
    public GameData getGame(int gameID) {
        // TODO: SELECT * FROM games WHERE id = ?
        return null;
    }

    @Override
    public void updateGame(GameData game) {
        // TODO: UPDATE games SET game=?, whitePlayer=?, blackPlayer=? WHERE id=?
    }

    @Override
    public List<GameData> listGames() {
        // TODO: SELECT * FROM games
        return new ArrayList<>();
    }

    @Override
    public void clear() {
        // TODO: DELETE FROM games
    }
}
