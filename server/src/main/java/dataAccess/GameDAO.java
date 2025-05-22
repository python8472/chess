package dataAccess;

import model.GameData;
import java.util.List;

public interface GameDAO {
    List<GameData> listGames();
    void clear(); // for DELETE /db
    // more methods for create/join will come later
}
