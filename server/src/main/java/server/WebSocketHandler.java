package server;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import request.LeaveRequest;
import result.LeaveResult;
import service.GameplayService;
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
    private final GameplayService gameplayService;

    private final Map<Integer, Set<Session>> gameSessions = new ConcurrentHashMap<>();
    private final Map<Session, String> sessionToUsername = new ConcurrentHashMap<>();

    public WebSocketHandler(GameDAO gameDAO, AuthDAO authDAO, GameplayService gameplayService) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
        this.gameplayService = gameplayService;
    }

    public void joinGame(int gameID, Session session) {
        gameSessions.computeIfAbsent(gameID, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void leaveGame(int gameID, Session session) {
        gameSessions.getOrDefault(gameID, Set.of()).remove(session);
        sessionToUsername.remove(session);
    }

    public void receiveMessage(String messageJson, int gameID, Session session) {
        try {
            UserGameCommand base = gson.fromJson(messageJson, UserGameCommand.class);
            String authToken = base.getAuthToken();
            String commandType = base.getCommandType().name();

            AuthData auth = authDAO.getAuth(authToken);
            if (auth == null) {
                send(session, new ErrorMessage("Invalid authToken"));
                return;
            }

            GameData game = gameDAO.getGame(gameID);
            if (game == null || game.game() == null) {
                send(session, new ErrorMessage("Game not found or is corrupted"));
                return;
            }

            sessionToUsername.put(session, auth.getUsername());

            switch (commandType) {
                case "CONNECT" -> {
                    ChessGame cg = game.game();
                    System.out.println("[DEBUG] On CONNECT - Game Board:\n" + cg.getBoard());
                    send(session, new LoadGameMessage(cg));
                    broadcastExcept(gameID, session, new NotificationMessage(notifyConnected(auth.getUsername(), game)));
                }


                case "MAKE_MOVE" -> {
                    MakeMoveCommand cmd = gson.fromJson(messageJson, MakeMoveCommand.class);
                    ChessGame g = game.game();

                    if (g.getGameOver()) {
                        send(session, new ErrorMessage("Game is already over"));
                        return;
                    }

                    ChessMove move = cmd.getMove();
                    if (g.getTeamTurn() == ChessGame.TeamColor.WHITE && !auth.getUsername().equals(game.getWhiteUsername())
                            || g.getTeamTurn() == ChessGame.TeamColor.BLACK && !auth.getUsername().equals(game.getBlackUsername())) {
                        send(session, new ErrorMessage("Not your turn"));
                        return;
                    }

                    if (!g.validMoves(move.getStartPosition()).contains(move)) {
                        send(session, new ErrorMessage("Illegal move"));
                        return;
                    }

                    g.makeMove(move);
                    gameDAO.updateGame(game);
                    broadcast(gameID, new LoadGameMessage(g));
                    broadcastExcept(gameID, session, new NotificationMessage(auth.getUsername() + " moved: " + move));

                    if (g.isInCheckmate(g.getTeamTurn())) {
                        broadcast(gameID, new NotificationMessage("Checkmate!"));
                    } else if (g.isInStalemate(g.getTeamTurn())) {
                        broadcast(gameID, new NotificationMessage("Stalemate."));
                    } else if (g.isInCheck(g.getTeamTurn())) {
                        broadcast(gameID, new NotificationMessage("Check!"));
                    }
                }

                case "HIGHLIGHT_MOVES" -> {
                    HighlightMovesCommand cmd = gson.fromJson(messageJson, HighlightMovesCommand.class);
                    ChessGame g = game.game();
                    ChessPosition pos = cmd.getPosition();
                    Collection<ChessMove> moves = g.validMoves(pos);
                    send(session, new NotificationMessage("Legal moves from " + pos.toString() + ": " + moves));
                }

                case "LEAVE" -> {
                    leaveGame(gameID, session);
                    LeaveRequest leaveRequest = new LeaveRequest(authToken, gameID);
                    LeaveResult leaveResult = gameplayService.leave(leaveRequest);

                    if (leaveResult.getMessage() != null) {
                        send(session, new ErrorMessage("Leave failed: " + leaveResult.getMessage()));
                    } else {
                        broadcastExcept(gameID, session, new NotificationMessage(auth.getUsername() + " left the game."));
                    }
                }

                case "RESIGN" -> {
                    if (!auth.getUsername().equals(game.getWhiteUsername()) &&
                            !auth.getUsername().equals(game.getBlackUsername())) {
                        send(session, new ErrorMessage("Only players may resign"));
                        return;
                    }

                    if (game.game().getGameOver()) {
                        send(session, new ErrorMessage("Game already over"));
                        return;
                    }

                    game.game().setGameOver(true);
                    gameDAO.updateGame(game);
                    broadcast(gameID, new NotificationMessage(auth.getUsername() + " resigned."));
                }

                default -> send(session, new ErrorMessage("Unknown command type: " + commandType));
            }

        } catch (Exception e) {
            e.printStackTrace(); // Server-side debug
            try {
                send(session, new ErrorMessage("Internal error: " + e.getClass().getSimpleName() + ": " + e.getMessage()));
            } catch (IOException ignored) {}
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
