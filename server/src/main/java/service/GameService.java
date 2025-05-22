package service;

import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import model.AuthData;
import model.GameData;
import result.ListGamesResult;
import request.CreateGameRequest;
import result.CreateGameResult;
import java.util.List;
import request.JoinGameRequest;
import result.JoinGameResult;


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

    public JoinGameResult joinGame(String authToken, JoinGameRequest request) {
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            return new JoinGameResult("Error: unauthorized");
        }

        if (request.getGameID() == null || request.getPlayerColor() == null) {
            return new JoinGameResult("Error: missing fields");
        }

        GameData game = gameDAO.getGame(request.getGameID());
        if (game == null) {
            return new JoinGameResult("Error: invalid game ID");
        }

        String username = auth.getUsername();
        String color = request.getPlayerColor().toUpperCase();

        if (color.equals("WHITE")) {
            if (game.getWhiteUsername() != null) {
                return new JoinGameResult("Error: white player already joined");
            }
            game = new GameData(game.getGameID(), game.getGameName(), username, game.getBlackUsername());
        } else if (color.equals("BLACK")) {
            if (game.getBlackUsername() != null) {
                return new JoinGameResult("Error: black player already joined");
            }
            game = new GameData(game.getGameID(), game.getGameName(), game.getWhiteUsername(), username);
        } else {
            return new JoinGameResult("Error: invalid player color");
        }

        gameDAO.updateGame(game);
        return new JoinGameResult(); // success (no message)
    }
}
