package server;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;
import dataAccess.AuthDAO;
import dataAccess.UserDAO;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryUserDAO;
import service.UserService;
import request.RegisterRequest;
import result.RegisterResult;

public class UserHandler {
    private final Gson gson = new Gson();
    private final UserService userService;

    public UserHandler() {
        // Instantiate the in-memory DAOs and wire them into the service
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
    }

    public Route handleRegister = (Request req, Response res) -> {
        try {
            RegisterRequest registerRequest = gson.fromJson(req.body(), RegisterRequest.class);
            RegisterResult result = userService.register(registerRequest);

            if (result.getMessage() != null) {
                res.status(403); // Registration failed
            } else {
                res.status(200); // Success
            }

            return gson.toJson(result);
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new RegisterResult("Error: " + e.getMessage()));
        }
    };
}
