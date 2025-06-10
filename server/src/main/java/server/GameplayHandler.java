package server;

import com.google.gson.Gson;
import request.*;
import result.*;
import service.GameplayService;
import spark.Request;
import spark.Response;
import spark.Route;

public class GameplayHandler {

    private GameplayService gameplayService = null;
    private final Gson gson = new Gson();

    public GameplayHandler(GameplayService gameplayService) {
        this.gameplayService = gameplayService;
    }

    public Route handleMove = (Request req, Response res) -> {
        MoveRequest request = gson.fromJson(req.body(), MoveRequest.class);
        MoveResult result = gameplayService.makeMove(request);
        res.type("application/json");
        return gson.toJson(result);
    };

    public Route handleResign = (Request req, Response res) -> {
        ResignRequest request = gson.fromJson(req.body(), ResignRequest.class);
        ResignResult result = gameplayService.resign(request);
        res.type("application/json");
        return gson.toJson(result);
    };

    public Route handleLeave = (Request req, Response res) -> {
        LeaveRequest request = gson.fromJson(req.body(), LeaveRequest.class);
        LeaveResult result = gameplayService.leave(request);
        res.type("application/json");
        return gson.toJson(result);
    };
}
