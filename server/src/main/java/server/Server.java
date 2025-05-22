package server;

import spark.Spark;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        UserHandler userHandler = new UserHandler();
        GameHandler gameHandler = new GameHandler();
        ClearHandler clearHandler = new ClearHandler();

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
