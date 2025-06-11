package server;

import dataaccess.*;
import dataaccess.sql.SQLUserDAO;
import dataaccess.sql.SQLAuthDAO;
import dataaccess.sql.SQLGameDAO;

import model.GameData;
import service.*;
import spark.Spark;
import server.WebSocketHandler;

public class Server {

    private ClearService clearService; // used for testing purpoes only

    public static void main(String[] args) {
        new Server().run(8080);
    }

    public int run(int desiredPort) {
        try {
            DatabaseManager.configureDatabase();
        } catch (Exception e) {
            System.err.println("DB Fail: " + e.getMessage());
            return -1;
        }

        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        // DAO + Service setup
        UserDAO userDAO = new SQLUserDAO();
        AuthDAO authDAO = new SQLAuthDAO();
        GameDAO gameDAO = new SQLGameDAO();

        UserService userService = new UserService(userDAO, authDAO);
        LoginService loginService = new LoginService(userDAO, authDAO);
        LogoutService logoutService = new LogoutService(authDAO);
        GameService gameService = new GameService(gameDAO, authDAO);
        clearService = new ClearService(userDAO, authDAO, gameDAO);
        GameplayService gameplayService = new GameplayService(gameDAO, authDAO);

        UserHandler userHandler = new UserHandler(userService, loginService, logoutService);
        GameHandler gameHandler = new GameHandler(gameService);
        ClearHandler clearHandler = new ClearHandler(clearService);
        GameplayHandler gameplayHandler = new GameplayHandler(gameplayService);
        WebSocketHandler wsHandler = new WebSocketHandler(gameDAO, authDAO, gameplayService);
        GameWebSocket.setHandler(wsHandler);
        Spark.webSocket("/ws", GameWebSocket.class);

        // Routes
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
