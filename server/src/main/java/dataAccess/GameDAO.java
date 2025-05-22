package dataAccess;

import model.GameData;
import java.util.List;

public interface GameDAO {
    List<GameData> listGames();
    int createGame(String gameName);
    GameData getGame(int gameID);
    void updateGame(GameData game);
    void clear();
}
