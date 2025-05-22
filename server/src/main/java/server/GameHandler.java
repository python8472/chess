package server;

import com.google.gson.Gson;
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
    private final Gson gson = new Gson();
    private GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Route handleListGames = (Request req, Response res) -> {
        String authToken = req.headers("Authorization");
        ListGamesResult result = gameService.listGames(authToken);

        if (result.getMessage() != null) {
            res.status(401);
        } else {
            res.status(200);
        }

        return gson.toJson(result);
    };

    public Route handleCreateGame = (Request req, Response res) -> {
        String authToken = req.headers("Authorization");
        CreateGameRequest request = gson.fromJson(req.body(), CreateGameRequest.class);

        if (request.getGameName() == null) {
            res.status(400);
            return gson.toJson(new CreateGameResult("Error: missing required fields"));
        }

        CreateGameResult result = gameService.createGame(authToken, request);
        if (result.getMessage() != null) {
            res.status(401);
        } else {
            res.status(200);
        }

        return gson.toJson(result);
    };

    public Route handleJoinGame = (Request req, Response res) -> {
        String authToken = req.headers("Authorization");
        JoinGameRequest request = gson.fromJson(req.body(), JoinGameRequest.class);

        // Defensive input validation (for bad color or null ID)
        if (request.getGameID() == null ||
                (request.getPlayerColor() != null &&
                        !request.getPlayerColor().equalsIgnoreCase("white") &&
                        !request.getPlayerColor().equalsIgnoreCase("black"))) {
            res.status(400);
            return gson.toJson(new JoinGameResult("Error: bad request"));
        }

        JoinGameResult result = gameService.joinGame(authToken, request);

        if (result.getMessage() != null) {
            if (result.getMessage().contains("bad request")) {
                res.status(400);
            } else if (result.getMessage().contains("unauthorized")) {
                res.status(401);
            } else {
                res.status(403);
            }
        } else {
            res.status(200);
        }

        return gson.toJson(result);
    };

}
