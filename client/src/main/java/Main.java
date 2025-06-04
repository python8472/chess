import chess.*;
import ui.PreLogin;


import server.Server;
import ui.PreLogin;

public class Main {
    public static void main(String[] args) {
        // Start the server on port 8080
        new Thread(() -> new Server().run(8080)).start();

        // Wait briefly to ensure the server starts up before the client connects
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println("Startup delay interrupted.");
        }

        // Launch client UI
        System.out.println("♕ 240 Chess Client");
        new PreLogin().run();
    }
}
