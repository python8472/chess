package websocket;

import com.google.gson.Gson;
import dataaccess.*;
import service.GameplayService;
import websocket.commands.*;
import websocket.messages.*;
import model.AuthData;

public class WebSocketHandler {

    private final ConnectionManager connections = ConnectionManager.getInstance();
    private final GameplayService gameplayService;
    private final Gson gson = new Gson();

    public WebSocketHandler(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.gameplayService = new GameplayService(userDAO, gameDAO, authDAO);
    }

    public void joinGame(int gameID, Connection connection) {
        connections.add(gameID, connection);
    }

    public void leaveGame(int gameID, Connection connection) {
        connections.remove(gameID, connection);
    }

    public void receiveMessage(String messageJson, int gameID, Connection connection) {
        UserGameCommand command = gson.fromJson(messageJson, UserGameCommand.class);

        switch (command.getCommandType()) {
            case "makeMove":
                MakeMoveCommand moveCmd = gson.fromJson(messageJson, MakeMoveCommand.class);
                gameplayService.makeMove(gameID, moveCmd, connection);
                break;

            case "resign":
                ResignCommand resignCmd = gson.fromJson(messageJson, ResignCommand.class);
                gameplayService.resignGame(gameID, resignCmd, connection);
                break;

            case "leave":
                LeaveCommand leaveCmd = gson.fromJson(messageJson, LeaveCommand.class);
                gameplayService.leaveGame(gameID, leaveCmd, connection);
                break;

            case "highlightMoves":
                HighlightMovesCommand highlightCmd = gson.fromJson(messageJson, HighlightMovesCommand.class);
                gameplayService.highlightMoves(gameID, highlightCmd, connection);
                break;

            default:
                connection.send(gson.toJson(new ErrorMessage("Unknown command type: " + command.getCommandType())));
                break;
        }
    }
}
