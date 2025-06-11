// WebSocketHandler.java
package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.*;
import websocket.messages.*;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketHandler {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;
    private final Gson gson = new Gson();

    // gameID -> list of sessions
    private final Map<Integer, Set<Session>> gameSessions = new ConcurrentHashMap<>();
    private final Map<Session, String> sessionToUsername = new ConcurrentHashMap<>();

    public WebSocketHandler(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public void joinGame(int gameID, Session session) {
        gameSessions.computeIfAbsent(gameID, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void leaveGame(int gameID, Session session) {
        gameSessions.getOrDefault(gameID, Set.of()).remove(session);
        sessionToUsername.remove(session);
    }

    public void receiveMessage(String messageJson, int gameID, Session session) {
        UserGameCommand base = gson.fromJson(messageJson, UserGameCommand.class);
        String authToken = base.getAuthToken();
        String commandType = base.getCommandType().name();

        try {
            AuthData auth = authDAO.getAuth(authToken);
            if (auth == null) {
                send(session, new ErrorMessage("Error: invalid authToken"));
                return;
            }
            GameData game = gameDAO.getGame(gameID);
            if (game == null) {
                send(session, new ErrorMessage("Error: invalid gameID"));
                return;
            }

            sessionToUsername.put(session, auth.getUsername());

            switch (commandType) {
                case "CONNECT" -> {
                    send(session, new LoadGameMessage(game.game()));
                    broadcastExcept(gameID, session, new NotificationMessage(notifyConnected(auth.getUsername(), game)));
                }
                case "MAKE_MOVE" -> {
                    MakeMoveCommand cmd = gson.fromJson(messageJson, MakeMoveCommand.class);
                    ChessGame g = game.game();
                    if (g.getGameOver()) {
                        send(session, new ErrorMessage("Error: game over"));
                        return;
                    }
                    ChessMove move = cmd.getMove();
                    // Check that it's the user's turn
                    if (g.getTeamTurn() == ChessGame.TeamColor.WHITE && !auth.getUsername().equals(game.getWhiteUsername())
                            || g.getTeamTurn() == ChessGame.TeamColor.BLACK && !auth.getUsername().equals(game.getBlackUsername())) {
                        send(session, new ErrorMessage("Error: not your turn"));
                        return;
                    }
                    if (!g.validMoves(move.getStartPosition()).contains(move)) {
                        send(session, new ErrorMessage("Error: illegal move"));
                        return;
                    }
                    g.makeMove(move);
                    gameDAO.updateGame(gameID, game);
                    broadcast(gameID, new LoadGameMessage(g));
                    broadcastExcept(gameID, session, new NotificationMessage(auth.getUsername() + " moved: " + move));
                    if (g.isInCheckmate(g.getTeamTurn())) broadcast(gameID, new NotificationMessage(auth.getUsername() + " is in checkmate!"));
                    else if (g.isInStalemate(g.getTeamTurn())) broadcast(gameID, new NotificationMessage("Game ended in stalemate."));
                    else if (g.isInCheck(g.getTeamTurn())) broadcast(gameID, new NotificationMessage(auth.getUsername() + " is in check!"));
                }
                case "LEAVE" -> {
                    broadcastExcept(gameID, session, new NotificationMessage(auth.getUsername() + " left the game."));
                    leaveGame(gameID, session);
                }
                case "RESIGN" -> {
                    // Only white or black player may resign
                    if (!auth.getUsername().equals(game.getWhiteUsername()) &&
                            !auth.getUsername().equals(game.getBlackUsername())) {
                        send(session, new ErrorMessage("Error: only players can resign"));
                        return;
                    }

                    // Check if game is done to prevent more resigning
                    if (game.game().getGameOver()) {
                        send(session, new ErrorMessage("Error: game already over"));
                        return;
                    }

                    // Mark game as over and send notif
                    game.game().setGameOver(true);
                    gameDAO.updateGame(gameID, game);
                    broadcast(gameID, new NotificationMessage(auth.getUsername() + " resigned."));
                }

                default -> send(session, new ErrorMessage("Error: unknown command"));
            }
        } catch (DataAccessException | IOException e) {
            try {
                send(session, new ErrorMessage("Error: " + e.getMessage()));
            } catch (IOException ignored) {
            }
        } catch (InvalidMoveException e) {
            throw new RuntimeException(e);
        }
    }

    private void send(Session session, ServerMessage msg) throws IOException {
        if (session.isOpen()) session.getRemote().sendString(gson.toJson(msg));
    }

    private void broadcast(int gameID, ServerMessage msg) {
        for (Session s : gameSessions.getOrDefault(gameID, Set.of())) {
            try {
                send(s, msg);
            } catch (IOException ignored) {}
        }
    }

    private void broadcastExcept(int gameID, Session excluded, ServerMessage msg) {
        for (Session s : gameSessions.getOrDefault(gameID, Set.of())) {
            if (!s.equals(excluded)) {
                try {
                    send(s, msg);
                } catch (IOException ignored) {}
            }
        }
    }

    private String notifyConnected(String username, GameData game) {
        if (username.equals(game.getWhiteUsername())) return username + " connected as white";
        if (username.equals(game.getBlackUsername())) return username + " connected as black";
        return username + " joined as observer";
    }
}
