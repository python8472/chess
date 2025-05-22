package server;

import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import result.ListGamesResult;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;
import request.CreateGameRequest;
import result.CreateGameResult;
import request.JoinGameRequest;
import result.JoinGameResult;


public class GameHandler {
    private final Gson gson = new Gson();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();
    private final GameService gameService = new GameService(gameDAO, authDAO);

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
        CreateGameRequest createReq = gson.fromJson(req.body(), CreateGameRequest.class);

        // Check for missing fields (400)
        if (createReq.getGameName() == null || createReq.getGameName().isBlank()) {
            res.status(400);
            return gson.toJson(new CreateGameResult("Error: missing game name"));
        }

        CreateGameResult result = gameService.createGame(authToken, createReq);

        if (result.getMessage() != null) {
            // check if this is due to auth failure
            if (result.getMessage().toLowerCase().contains("unauthorized")) {
                res.status(401);
            } else {
                res.status(400); // fallback for other invalid input
            }
        } else {
            res.status(200); // Game created
        }

        return gson.toJson(result);
    };

    public Route handleJoinGame = (Request req, Response res) -> {
        String authToken = req.headers("Authorization");
        JoinGameRequest joinReq = gson.fromJson(req.body(), JoinGameRequest.class);
        JoinGameResult result = gameService.joinGame(authToken, joinReq);

        if (result.getMessage() != null) {
            res.status(403); // Invalid game or color
        } else {
            res.status(200); // Joined
        }

        return gson.toJson(result);
    };
}
