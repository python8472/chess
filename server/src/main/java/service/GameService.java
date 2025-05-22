package service;

import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import model.AuthData;
import model.GameData;
import result.ListGamesResult;
import request.CreateGameRequest;
import result.CreateGameResult;
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

    public CreateGameResult createGame(String authToken, CreateGameRequest request) {
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            return new CreateGameResult("Error: unauthorized");
        }

        if (request.getGameName() == null || request.getGameName().isBlank()) {
            return new CreateGameResult("Error: game name required");
        }

        int gameID = gameDAO.createGame(request.getGameName());
        return new CreateGameResult(gameID);
    }
}
