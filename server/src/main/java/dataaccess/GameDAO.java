    package dataaccess;

    import model.GameData;

    import java.util.List;

    public interface GameDAO {
        int createGame(String gameName) throws DataAccessException;

        GameData getGame(int gameID) throws DataAccessException;

        void updateGame(GameData game) throws DataAccessException;

        List<GameData> listGames() throws DataAccessException;

        void clear() throws DataAccessException;
    }
