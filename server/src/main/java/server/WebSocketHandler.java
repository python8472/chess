package websocket;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.AuthData;
import request.*;
import result.*;
import service.GameplayService;
import websocket.commands.*;
import websocket.messages.*;

public class WebSocketHandler {

    private final ConnectionManager connections = ConnectionManager.getInstance();
    private final GameplayService gameplayService;
    private final Gson gson = new Gson();

    public WebSocketHandler(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameplayService = new GameplayService(gameDAO, authDAO);
    }

    public void joinGame(int gameID, Connection connection) {
        connections.add(gameID, connection);
    }

    public void leaveGame(int gameID, Connection connection) {
        connections.remove(gameID, connection);
    }

    public void receiveMessage(String messageJson, int gameID, Connection connection) {
        UserGameCommand baseCommand = gson.fromJson(messageJson, UserGameCommand.class);
        String commandType = String.valueOf(baseCommand.getCommandType());

        switch (commandType) {
            case "makeMove" -> {
                MakeMoveCommand cmd = gson.fromJson(messageJson, MakeMoveCommand.class);
                MoveRequest request = new MoveRequest(cmd.getAuthToken(), gameID, cmd.getPlayerColor(), cmd.getMove());
                MoveResult result = gameplayService.makeMove(request);
                connection.send(gson.toJson(new NotificationMessage(result.getMessage() != null ? result.getMessage() : "Move successful.")));
                if (result.getMessage() != null && result.getMessage().startsWith("Game over")) {
                    connections.broadcast(gameID, gson.toJson(new NotificationMessage(result.getMessage())));
                } else {
                    connections.broadcast(gameID, gson.toJson(new NotificationMessage(cmd.getPlayerColor() + " moved.")));
                }
            }

            case "resign" -> {
                ResignCommand cmd = gson.fromJson(messageJson, ResignCommand.class);
                ResignRequest request = new ResignRequest(cmd.getAuthToken(), gameID);
                ResignResult result = gameplayService.resign(request);
                connections.broadcast(gameID, gson.toJson(new NotificationMessage(
                        result.getMessage() != null ? result.getMessage() : "A player has resigned."
                )));
            }

            case "leave" -> {
                LeaveCommand cmd = gson.fromJson(messageJson, LeaveCommand.class);
                LeaveRequest request = new LeaveRequest(cmd.getAuthToken(), gameID);
                LeaveResult result = gameplayService.leave(request);
                connection.send(gson.toJson(new NotificationMessage(
                        result.getMessage() != null ? result.getMessage() : "You left the game."
                )));
                leaveGame(gameID, connection);
            }

            case "highlightMoves" -> {
                HighlightMovesCommand cmd = gson.fromJson(messageJson, HighlightMovesCommand.class);
                // Assume you implement highlightMoves in GameplayService later
                connection.send(gson.toJson(new ErrorMessage("Highlight moves not yet implemented")));
            }

            default -> {
                connection.send(gson.toJson(new ErrorMessage("Unknown command type: " + commandType)));
            }
        }
    }
}
