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
import service.LoginService;
import request.RegisterRequest;
import request.LoginRequest;
import result.RegisterResult;
import result.LoginResult;
import service.LogoutService;
import result.LogoutResult;


public class UserHandler {
    private final Gson gson = new Gson();
    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final UserService userService = new UserService(userDAO, authDAO);
    private final LoginService loginService = new LoginService(userDAO, authDAO);

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

    public Route handleLogin = (Request req, Response res) -> {
        try {
            LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);
            LoginResult result = loginService.login(loginRequest);

            if (result.getMessage() != null) {
                res.status(401); // Unauthorized
            } else {
                res.status(200); // Success
            }

            return gson.toJson(result);
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new LoginResult("Error: " + e.getMessage()));
        }
    };

    public Route handleLogout = (Request req, Response res) -> {
        try {
            String authToken = req.headers("Authorization");
            LogoutService logoutService = new LogoutService(authDAO);
            LogoutResult result = logoutService.logout(authToken);

            if (result.getMessage() != null) {
                res.status(401); // Unauthorized or missing
            } else {
                res.status(200); // Success
            }

            return gson.toJson(result);
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new LogoutResult("Error: " + e.getMessage()));
        }
    };
}
