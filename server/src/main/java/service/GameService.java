package service;

import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import model.AuthData;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.JoinGameResult;
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

    public CreateGameResult createGame(String authToken, CreateGameRequest request) {
        if (authToken == null || authDAO.getAuth(authToken) == null) {
            return new CreateGameResult("Error: unauthorized");
        }

        if (request.getGameName() == null || request.getGameName().isBlank()) {
            return new CreateGameResult("Error: game name required");
        }

        int gameID = gameDAO.createGame(request.getGameName());
        return new CreateGameResult(gameID);
    }

    public JoinGameResult joinGame(String authToken, JoinGameRequest request) {
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            return new JoinGameResult("Error: unauthorized");
        }

        if (request.getGameID() == null || gameDAO.getGame(request.getGameID()) == null) {
            return new JoinGameResult("Error: bad request");
        }

        GameData game = gameDAO.getGame(request.getGameID());
        String username = auth.getUsername();

        String color = request.getPlayerColor();
        if (color == null) {
            // Observer join
            return new JoinGameResult(); // success
        }

        color = color.toUpperCase();
        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            return new JoinGameResult("Error: bad request");
        }

        switch (color) {
            case "WHITE" -> {
                if (game.getWhiteUsername() != null) {
                    return new JoinGameResult("Error: white player already joined");
                }
                game = new GameData(game.getGameID(), game.getGameName(), username, game.getBlackUsername());
            }
            case "BLACK" -> {
                if (game.getBlackUsername() != null) {
                    return new JoinGameResult("Error: black player already joined");
                }
                game = new GameData(game.getGameID(), game.getGameName(), game.getWhiteUsername(), username);
            }
        }

        gameDAO.updateGame(game);
        return new JoinGameResult(); // success
    }
}
