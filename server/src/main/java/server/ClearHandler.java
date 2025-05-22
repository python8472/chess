package server;

import com.google.gson.Gson;
import dataAccess.*;
import spark.Request;
import spark.Response;
import spark.Route;
import result.ClearResult;

public class ClearHandler {

    private final Gson gson = new Gson();

    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();

    public Route handle = (Request req, Response res) -> {
        try {
            userDAO.clear();
            authDAO.clear();
            gameDAO.clear();

            res.status(200);
            return gson.toJson(new ClearResult());
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new ClearResult("Error: " + e.getMessage()));
        }
    };
}
