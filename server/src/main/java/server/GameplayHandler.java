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

    public final Route handleLeave = (Request req, Response res) -> {
        try {
            LeaveRequest requestObj = gson.fromJson(req.body(), LeaveRequest.class);

            LeaveResult result = gameplayService.leave(requestObj);

            if (result.getMessage() != null) {
                if (result.getMessage().contains("unauthorized")) {
                    res.status(401);
                } else {
                    res.status(400);
                }
            } else {
                res.status(200);
            }

            return gson.toJson(result);
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new LeaveResult("Error: server failure"));
        }
    };
}
