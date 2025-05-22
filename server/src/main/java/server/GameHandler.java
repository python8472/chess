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
}
