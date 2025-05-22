package server;

import spark.*;
import static spark.Spark.*;

public class Server {

    public int run(int desiredPort) {
        port(desiredPort);
        staticFiles.location("web");

        // Register endpoints
        UserHandler userHandler = new UserHandler();
        post("/user", userHandler.handleRegister);

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
