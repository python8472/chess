package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import request.*;
import result.*;
import service.GameplayService;
import websocket.commands.*;
import websocket.messages.*;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.*;

public class WebSocketHandler {

    private final GameplayService gameplayService;
    private final Gson gson = new Gson();
    private final GameDAO gameDAO;

    private final Map<Integer, Set<Session>> connections = new HashMap<>();

    public WebSocketHandler(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameplayService = new GameplayService(gameDAO, authDAO);
        this.gameDAO = gameDAO;
    }

    public void joinGame(int gameID, Session session) {
        connections.computeIfAbsent(gameID, k -> new HashSet<>()).add(session);
    }

    public void leaveGame(int gameID, Session session) {
        if (connections.containsKey(gameID)) {
            connections.get(gameID).remove(session);
        }
    }

    public void broadcast(int gameID, String message) {
        var sessions = connections.getOrDefault(gameID, Collections.emptySet());
        for (var s : sessions) {
            try {
                if (s.isOpen()) s.getRemote().sendString(message);
            } catch (IOException e) {
                System.err.println("Broadcast error: " + e.getMessage());
            }
        }
    }

    public void receiveMessage(String messageJson, int gameID, Session session) {
        UserGameCommand baseCommand = gson.fromJson(messageJson, UserGameCommand.class);

        try {
            switch (baseCommand.getCommandType()) {
                case CONNECT -> {
                    ChessGame game = gameDAO.getGame(gameID).game();
                    session.getRemote().sendString(gson.toJson(new LoadGameMessage(game)));
                    broadcast(gameID, gson.toJson(new NotificationMessage("A user connected.")));
                }

                case MAKE_MOVE -> {
                    MakeMoveCommand cmd = gson.fromJson(messageJson, MakeMoveCommand.class);
                    MoveRequest request = new MoveRequest(cmd.getAuthToken(), gameID, cmd.getPlayerColor(), cmd.getMove());
                    MoveResult result = gameplayService.makeMove(request);

                    if (result.getMessage() != null && result.getMessage().toLowerCase().contains("error")) {
                        session.getRemote().sendString(gson.toJson(new ErrorMessage(result.getMessage())));
                    } else {
                        ChessGame updated = gameDAO.getGame(gameID).game();
                        broadcast(gameID, gson.toJson(new LoadGameMessage(updated)));
                        broadcast(gameID, gson.toJson(new NotificationMessage(cmd.getPlayerColor() + " moved")));
                    }
                }

                case RESIGN -> {
                    ResignCommand cmd = gson.fromJson(messageJson, ResignCommand.class);
                    ResignRequest request = new ResignRequest(cmd.getAuthToken(), gameID);
                    ResignResult result = gameplayService.resign(request);

                    if (result.getMessage() != null && result.getMessage().toLowerCase().contains("error")) {
                        session.getRemote().sendString(gson.toJson(new ErrorMessage(result.getMessage())));
                    } else {
                        broadcast(gameID, gson.toJson(new NotificationMessage("A player has resigned.")));
                    }
                }

                case LEAVE -> {
                    LeaveCommand cmd = gson.fromJson(messageJson, LeaveCommand.class);
                    LeaveRequest request = new LeaveRequest(cmd.getAuthToken(), gameID);
                    LeaveResult result = gameplayService.leave(request);

                    if (result.getMessage() != null && result.getMessage().toLowerCase().contains("error")) {
                        session.getRemote().sendString(gson.toJson(new ErrorMessage(result.getMessage())));
                    } else {
                        broadcast(gameID, gson.toJson(new NotificationMessage("A player left the game.")));
                        leaveGame(gameID, session);
                    }
                }

                default -> session.getRemote().sendString(gson.toJson(new ErrorMessage("Unknown command type")));
            }
        } catch (DataAccessException | IOException e) {
            try {
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Internal server error: " + e.getMessage())));
            } catch (IOException ignored) {}
        }
    }
}
