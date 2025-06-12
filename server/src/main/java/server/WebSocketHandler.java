package server;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.JsonObject;
import dataaccess.AuthDAO;
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
import chess.ChessPiece;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
            JsonObject jsonObj = gson.fromJson(messageJson, JsonObject.class);
            String commandType = jsonObj.get("commandType").getAsString();
            String authToken = jsonObj.get("authToken").getAsString();
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
                    send(session, new LoadGameMessage(game.game()));
                    broadcastExcept(gameID, session,
                            new NotificationMessage(notifyConnected(auth.getUsername(), game)));
                }

                case "MAKE_MOVE" -> {
                    MakeMoveCommand cmd = gson.fromJson(messageJson, MakeMoveCommand.class);
                    ChessGame g = game.game();

                    if (g.getGameOver()) {
                        send(session, new ErrorMessage("Game is already over"));
                        return;
                    }

                    ChessMove move = cmd.getMove();
                    if ((g.getTeamTurn() == ChessGame.TeamColor.WHITE && !auth.getUsername().equals(game.getWhiteUsername())) ||
                            (g.getTeamTurn() == ChessGame.TeamColor.BLACK && !auth.getUsername().equals(game.getBlackUsername()))) {
                        send(session, new ErrorMessage("Not your turn"));
                        return;
                    }

                    if (!g.validMoves(move.getStartPosition()).contains(move)) {
                        send(session, new ErrorMessage("Illegal move"));
                        return;
                    }

                    g.makeMove(move);
                    gameDAO.updateGame(game);

                    // Notify all clients with updated board
                    broadcast(gameID, new LoadGameMessage(g));

                    // More readable move stuff
                    String from = toAlgebraic(move.getStartPosition());
                    String to = toAlgebraic(move.getEndPosition());
                    String teamMoved = (g.getTeamTurn() == ChessGame.TeamColor.BLACK) ? "White" : "Black";
                    String moveString = teamMoved + " moved " + from + " to " + to;
                    broadcastExcept(gameID, session, new NotificationMessage(moveString));

                    // Notify game status with usernames!!!! instead of team colors
                    ChessGame.TeamColor currentTurn = g.getTeamTurn();
                    String currentUser = currentTurn == ChessGame.TeamColor.WHITE ? game.getWhiteUsername() : game.getBlackUsername();
                    String opponentUser = currentTurn == ChessGame.TeamColor.WHITE ? game.getBlackUsername() : game.getWhiteUsername();

                    if (g.isInCheckmate(currentTurn)) {
                        broadcast(gameID, new NotificationMessage(currentUser + " is in checkmate! " + opponentUser + " wins!"));
                    } else if (g.isInStalemate(currentTurn)) {
                        broadcast(gameID, new NotificationMessage("Stalemate. " + currentUser + " has no legal moves."));
                    } else if (g.isInCheck(currentTurn)) {
                        broadcast(gameID, new NotificationMessage(currentUser + " is in check!"));
                    }
                }

                case "HIGHLIGHT_MOVES" -> {
                    HighlightMovesCommand cmd = gson.fromJson(messageJson, HighlightMovesCommand.class);
                    ChessPosition pos = cmd.getPosition();

                    if (pos == null) {
                        send(session, new ErrorMessage("Invalid position for highlight."));
                        return;
                    }

                    ChessPiece piece = game.game().getBoard().getPiece(pos);
                    if (piece == null) {
                        send(session, new ErrorMessage("No piece on selected square."));
                        return;
                    }

                    Collection<ChessMove> moves = game.game().validMoves(pos);
                    Set<ChessPosition> targets = moves.stream().map(ChessMove::getEndPosition).collect(Collectors.toSet());
                    send(session, new HighlightMessage(targets));
                }

                case "LEAVE" -> {
                    leaveGame(gameID, session);
                    LeaveRequest leaveRequest = new LeaveRequest(authToken, gameID);
                    LeaveResult leaveResult = gameplayService.leave(leaveRequest);
                    if (leaveResult.getMessage() != null) {
                        send(session, new ErrorMessage("Leave failed: " + leaveResult.getMessage()));
                    } else {
                        broadcastExcept(gameID, session,
                                new NotificationMessage(auth.getUsername() + " left the game."));
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
                send(session, new ErrorMessage("Not one of your pieces. Try again"));
            } catch (IOException ignored) {}
        }
    }


    private void send(Session session, ServerMessage msg) throws IOException {
        if (session.isOpen()) {session.getRemote().sendString(gson.toJson(msg));}
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
        if (username.equals(game.getWhiteUsername())) {return username + " connected as white";}
        if (username.equals(game.getBlackUsername())) {return username + " connected as black";}
        return username + " joined as observer";
    }

    private String toAlgebraic(ChessPosition pos) {
        char col = (char) ('a' + pos.getColumn() - 1); // 1→'a', 2→'b', etc.
        return "" + col + pos.getRow(); // e.g., "e2"
    }

}
