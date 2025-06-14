package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
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

    public ListGamesResult listGames(String authToken) throws DataAccessException {
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            return new ListGamesResult("Error: unauthorized");
        }
        List<GameData> games = gameDAO.listGames();
        return new ListGamesResult(games);
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest request) throws DataAccessException {
        if (authToken == null || authDAO.getAuth(authToken) == null) {
            return new CreateGameResult("Error: unauthorized");
        }
        if (request.getGameName() == null || request.getGameName().isBlank()) {
            return new CreateGameResult("Error: game name required");
        }
        int gameID = gameDAO.createGame(request.getGameName());
        return new CreateGameResult(gameID);
    }

    public JoinGameResult joinGame(String authToken, JoinGameRequest request) throws DataAccessException {
        // Validate auth token
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            return new JoinGameResult("Error: unauthorized");
        }

        // Validate request object and game ID
        Integer gameID = request.getGameID();
        String color = request.getPlayerColor();

        if (gameID == null || color == null ||
                (!color.equalsIgnoreCase("WHITE") && !color.equalsIgnoreCase("BLACK") && !color.equalsIgnoreCase("OBSERVER"))) {
            return new JoinGameResult("Error: bad request");
        }

        // Always get fresh GameData from DB
        GameData game = gameDAO.getGame(gameID);
        if (game == null) {
            return new JoinGameResult("Error: bad request");
        }

        String username = auth.getUsername();

        switch (color.toUpperCase()) {
            case "WHITE" -> {
                String curr = game.getWhiteUsername();
                if (curr != null && !curr.isBlank()) {
                    return new JoinGameResult("Error: white player already joined");
                }
                game = new GameData(game.getGameID(), game.getGameName(), username, game.getBlackUsername(), game.game());
            }
            case "BLACK" -> {
                String curr = game.getBlackUsername();
                if (curr != null && !curr.isBlank()) {
                    return new JoinGameResult("Error: black player already joined");
                }
                game = new GameData(game.getGameID(), game.getGameName(), game.getWhiteUsername(), username, game.game());
            }
            case "OBSERVER" -> {
                return new JoinGameResult(); // No updates needed for observers
            }
        }

        // Only update game if a player joined
        gameDAO.updateGame(game);
        return new JoinGameResult();
    }

}
