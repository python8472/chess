package service;

import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import model.AuthData;
import model.GameData;
import result.ListGamesResult;

import java.util.List;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListGamesResult listGames(String authToken) {
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            return new ListGamesResult("Error: unauthorized");
        }

        List<GameData> games = gameDAO.listGames();
        return new ListGamesResult(games);
    }
}
