package server;

import dataaccess.*;
import service.UserService;
import service.LoginService;
import service.LogoutService;
import service.GameService;
import service.ClearService;
import spark.Spark;

public class Server {

    public int run(int desiredPort) {
        try {
            DatabaseManager.configureDatabase();  // make DB and fail if fail
        } catch (Exception e) {
            System.err.println("DB Fail: " + e.getMessage());
            return -1;
        }
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        //shared DAO instances
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        //shared service instances
        UserService userService = new UserService(userDAO, authDAO);
        LoginService loginService = new LoginService(userDAO, authDAO);
        LogoutService logoutService = new LogoutService(authDAO);
        GameService gameService = new GameService(gameDAO, authDAO);
        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);

        //handlers
        UserHandler userHandler = new UserHandler(userService, loginService, logoutService);
        GameHandler gameHandler = new GameHandler(gameService);
        ClearHandler clearHandler = new ClearHandler(clearService);

        //make routes
        Spark.post("/user", userHandler.handleRegister);
        Spark.post("/session", userHandler.handleLogin);
        Spark.delete("/session", userHandler.handleLogout);

        Spark.get("/game", gameHandler.handleListGames);
        Spark.post("/game", gameHandler.handleCreateGame);
        Spark.put("/game", gameHandler.handleJoinGame);

        Spark.delete("/db", clearHandler.handle);

        Spark.init();
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
