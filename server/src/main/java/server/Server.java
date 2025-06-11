package server;

import dataaccess.*;
import dataaccess.sql.SQLUserDAO;
import dataaccess.sql.SQLAuthDAO;
import dataaccess.sql.SQLGameDAO;

import model.GameData;
import service.*;
import spark.Spark;
import websocket.WebSocketHandler;

public class Server {

    private ClearService clearService; // used for testing purpoes only
    public static void main(String[] args) {
        new Server().run(8080);
    }

    public int run(int desiredPort) {
        try {
            DatabaseManager.configureDatabase();  // make DB and fail if fail
        } catch (Exception e) {
            System.err.println("DB Fail: " + e.getMessage());
            return -1;
        }

        Spark.port(desiredPort);
        Spark.webSocket("/ws", GameWebSocket.class);
        Spark.staticFiles.location("web");

        // shared DAO instances
        UserDAO userDAO = new SQLUserDAO();
        AuthDAO authDAO = new SQLAuthDAO();
        GameDAO gameDAO = new SQLGameDAO();

        // service instances
        UserService userService = new UserService(userDAO, authDAO);
        LoginService loginService = new LoginService(userDAO, authDAO);
        LogoutService logoutService = new LogoutService(authDAO);
        GameService gameService = new GameService(gameDAO, authDAO);
        clearService = new ClearService(userDAO, authDAO, gameDAO); // save to field

        // handlers
        UserHandler userHandler = new UserHandler(userService, loginService, logoutService);
        GameHandler gameHandler = new GameHandler(gameService);
        ClearHandler clearHandler = new ClearHandler(clearService);
        GameplayService gameplayService = new GameplayService(gameDAO, authDAO);
        GameplayHandler gameplayHandler = new GameplayHandler(gameplayService);
        WebSocketHandler wsHandler = new WebSocketHandler(gameDAO, authDAO, gameplayService);
        GameWebSocket.setHandler(wsHandler);

        // routes
        Spark.post("/user", userHandler.handleRegister);
        Spark.post("/session", userHandler.handleLogin);
        Spark.delete("/session", userHandler.handleLogout);

        Spark.get("/game", gameHandler.handleListGames);
        Spark.post("/game", gameHandler.handleCreateGame);
        Spark.put("/game", gameHandler.handleJoinGame);

        Spark.post("/game/move", gameplayHandler.handleMove);
        Spark.post("/game/resign", gameplayHandler.handleResign);
        Spark.post("/game/leave", gameplayHandler.handleLeave);

        Spark.delete("/db", clearHandler.handle);

        Spark.init();
        Spark.awaitInitialization();

        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public void clear() {
        if (clearService != null) {
            try {
                clearService.clearAll();
            } catch (DataAccessException e) {
                System.err.println("Failed to clear db: " + e.getMessage());
            }
        }
    }
}
