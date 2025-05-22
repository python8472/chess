package server;

import com.google.gson.Gson;
import result.ClearResult;
import service.ClearService;
import spark.Request;
import spark.Response;
import spark.Route;

public class ClearHandler {
    private final Gson gson = new Gson();
    private ClearService clearService;

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    public Route handle = (Request req, Response res) -> {
        try {
            ClearResult result = clearService.clearAll();
            res.status(200);
            return gson.toJson(result);
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new ClearResult("Error: " + e.getMessage()));
        }
    };
}
