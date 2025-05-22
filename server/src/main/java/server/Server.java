package server;

import spark.*;
import static spark.Spark.*;

public class Server {

    public static void main(String[] args) {
        new Server().run(8080);
    }

    public int run(int desiredPort) {
        port(desiredPort);
        staticFiles.location("web");

        // get endpoints
        UserHandler userHandler = new UserHandler();
        post("/user", userHandler.handleRegister);
        post("/session", userHandler.handleLogin);
        delete("/session", userHandler.handleLogout);

        GameHandler gameHandler = new GameHandler();
        get("/game", gameHandler.handleListGames);
        post("/game", gameHandler.handleCreateGame);
        put("/game", gameHandler.handleJoinGame);


        // Initialize Spark
        init();
        awaitInitialization();
        return port();
    }

    public void stop() {
        stop();
        awaitStop();
    }
}
