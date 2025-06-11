package ui;

import facade.ServerFacade;
import request.*;
import result.*;

import java.util.Scanner;

public class PreLogin {
    private final Scanner scanner = new Scanner(System.in);
    private final ServerFacade facade = new ServerFacade("http://localhost:8080");


    public void run() {
        System.out.println(EscapeSequences.ERASE_SCREEN +
                EscapeSequences.SET_TEXT_BOLD +
                "Welcome to Chess!" +
                EscapeSequences.RESET_TEXT_BOLD_FAINT);
        HelpHelper.printPreLoginHelp();

        while (true) {
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + "> " + EscapeSequences.RESET_TEXT_COLOR);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {continue;}

            String[] tokens = input.split("\\s+");
            String command = tokens[0].toLowerCase();

            try {
                switch (command) {
                    case "help" -> HelpHelper.printPreLoginHelp();
                    case "quit" -> {
                        System.out.println("Goodbye!");
                        return;
                    }
                    case "register" -> handleRegister(tokens);
                    case "login" -> handleLogin(tokens);
                    default -> System.out.println(EscapeSequences.SET_TEXT_COLOR_RED +
                            "Unknown command: " +
                            command +
                            EscapeSequences.RESET_TEXT_COLOR);
                }
            } catch (Exception e) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED +
                        "Error: " +
                        e.getMessage() +
                        EscapeSequences.RESET_TEXT_COLOR);
            }
        }
    }

    private void handleRegister(String[] tokens) throws Exception {
        if (tokens.length < 4) {
            System.out.println("Usage: register <username> <password> <email>");
            return;
        }

        var req = new RegisterRequest(tokens[1], tokens[2], tokens[3]);
        RegisterResult result = facade.register(req);

        if (result.getMessage() == null) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN +
                    "Registration successful. You're now logged in." +
                    EscapeSequences.RESET_TEXT_COLOR);
            new PostLogin(tokens[1], result.getAuthToken()).run();
        } else {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED +
                    "Registration failed: " +
                    result.getMessage() +
                    EscapeSequences.RESET_TEXT_COLOR);
        }
    }

    private void handleLogin(String[] tokens) throws Exception {
        if (tokens.length < 3) {
            System.out.println("Usage: login <username> <password>");
            return;
        }

        var req = new LoginRequest(tokens[1], tokens[2]);
        LoginResult result = facade.login(req);

        if (result.getMessage() == null) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN +
                    "Login successful." +
                    EscapeSequences.RESET_TEXT_COLOR);
            new PostLogin(tokens[1], result.getAuthToken()).run();
        } else {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED +
                    "Login failed: " +
                    result.getMessage() +
                    EscapeSequences.RESET_TEXT_COLOR);
        }
    }
}
