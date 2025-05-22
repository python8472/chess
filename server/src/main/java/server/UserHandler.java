package server;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;
import request.RegisterRequest;
import request.LoginRequest;
import result.RegisterResult;
import result.LoginResult;
import result.LogoutResult;
import service.UserService;
import service.LoginService;
import service.LogoutService;

public class UserHandler {
    private final Gson gson = new Gson();
    private UserService userService;
    private LoginService loginService;
    private LogoutService logoutService;

    public UserHandler(UserService userService, LoginService loginService, LogoutService logoutService) {
        this.userService = userService;
        this.loginService = loginService;
        this.logoutService = logoutService;
    }

    public Route handleRegister = (Request req, Response res) -> {
        try {
            RegisterRequest registerRequest = gson.fromJson(req.body(), RegisterRequest.class);

            if (registerRequest.getUsername() == null || registerRequest.getPassword() == null || registerRequest.getEmail() == null) {
                res.status(400);
                return gson.toJson(new RegisterResult("Error: missing required fields"));
            }

            RegisterResult result = userService.register(registerRequest);
            if (result.getMessage() != null) {
                res.status(403);
            } else {
                res.status(200);
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

            if (loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
                res.status(400);
                return gson.toJson(new LoginResult("Error: missing required fields"));
            }

            LoginResult result = loginService.login(loginRequest);
            if (result.getMessage() != null) {
                res.status(401);
            } else {
                res.status(200);
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
            LogoutResult result = logoutService.logout(authToken);

            if (result.getMessage() != null) {
                res.status(401);
            } else {
                res.status(200);
            }

            return gson.toJson(result);
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new LogoutResult("Error: " + e.getMessage()));
        }
    };
}
