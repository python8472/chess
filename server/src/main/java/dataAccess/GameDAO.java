package dataAccess;

import model.GameData;

import java.util.List;

public interface GameDAO {
    int createGame(String gameName);

    GameData getGame(int gameID);

    void updateGame(GameData game);

    List<GameData> listGames();

    void clear();
}
