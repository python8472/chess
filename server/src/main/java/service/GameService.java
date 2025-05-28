package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
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

        Integer gameID = request.getGameID();
        if (gameID == null || gameDAO.getGame(gameID) == null) {
            return new JoinGameResult("Error: bad request");
        }

        String color = request.getPlayerColor();
        if (color == null) {
            return new JoinGameResult("Error: bad request");
        }

        color = color.trim().toUpperCase();
        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            return new JoinGameResult("Error: bad request");
        }

        GameData game = gameDAO.getGame(gameID);
        String username = auth.getUsername();

        switch (color) {
            case "WHITE" -> {
                if (game.getWhiteUsername() != null) {
                    return new JoinGameResult("Error: white player already joined");
                }
                game = new GameData(game.getGameID(), game.getGameName(), username, game.getBlackUsername(), game.game());
            }
            case "BLACK" -> {
                if (game.getBlackUsername() != null) {
                    return new JoinGameResult("Error: black player already joined");
                }
                game = new GameData(game.getGameID(), game.getGameName(), game.getWhiteUsername(), username, game.game());
            }
        }

        gameDAO.updateGame(game);
        return new JoinGameResult();  // success
    }

}
