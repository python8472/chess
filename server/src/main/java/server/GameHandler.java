package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.JoinGameResult;
import result.ListGamesResult;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class GameHandler {
    private GameService gameService;
    private final Gson gson = new Gson();

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public final Route handleListGames = (Request req, Response res) -> {
        try {
            String authToken = req.headers("Authorization");
            ListGamesResult result = gameService.listGames(authToken);
            if (result.getMessage() != null) {
                res.status(401);
            } else {
                res.status(200);
            }
            return gson.toJson(result);
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(new ListGamesResult("Error: database failure"));
        }
    };

    public final Route handleCreateGame = (Request req, Response res) -> {
        try {
            String authToken = req.headers("Authorization");
            CreateGameRequest request = gson.fromJson(req.body(), CreateGameRequest.class);

            if (request == null || request.getGameName() == null || request.getGameName().isBlank()) {
                res.status(400);
                return gson.toJson(new CreateGameResult("Error: game name required"));
            }

            CreateGameResult result = gameService.createGame(authToken, request);
            if (result.getMessage() != null) {
                res.status(401);
            } else {
                res.status(200);
            }
            return gson.toJson(result);
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(new CreateGameResult("Error: database failure"));
        }
    };


    public final Route handleJoinGame = (Request req, Response res) -> {
        try {
            String authToken = req.headers("Authorization");
            JoinGameRequest request = gson.fromJson(req.body(), JoinGameRequest.class);

            if (request == null || request.getGameID() == null || request.getPlayerColor() == null) {
                res.status(400);
                return gson.toJson(new JoinGameResult("Error: bad request"));
            }

            JoinGameResult result = gameService.joinGame(authToken, request);

            if (result.getMessage() != null) {
                String msg = result.getMessage().toLowerCase();
                if (msg.contains("unauthorized")) {
                    res.status(401);
                } else if (msg.contains("already joined") || msg.contains("already taken")) {
                    res.status(403); // Slot conflict
                } else {
                    res.status(400); // Generic failure
                }
            } else {
                res.status(200);
            }

            return gson.toJson(result);
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(new JoinGameResult("Error: database failure"));
        }
    };

}
