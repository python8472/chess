package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import request.*;
import result.*;
import chess.GameHelper;

import java.util.Collection;

public class GameplayService {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameplayService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public MoveResult makeMove(MoveRequest request) {
        try {
            AuthData auth = authDAO.getAuth(request.authToken());
            if (auth == null) {
                return new MoveResult("Error: invalid auth token");
            }

            GameData gameData = gameDAO.getGame(request.gameID());
            if (gameData == null) {
                return new MoveResult("Error: invalid game ID");
            }

            ChessGame game = gameData.game();

            // Validate turn
            if (game.getTeamTurn() != request.playerColor()) {
                return new MoveResult("Error: not your turn");
            }

            // Validate move
            Collection<ChessMove> legalMoves = game.validMoves(request.move().getStartPosition());
            if (legalMoves == null || !legalMoves.contains(request.move())) {
                return new MoveResult("Error: illegal move");
            }

            // Apply move
            game.makeMove(request.move());
            gameDAO.updateGame(gameData); // Persist updated game state

            // Endgame checks
            ChessGame.TeamColor nextTurn = game.getTeamTurn();
            if (game.isInCheckmate(nextTurn)) {
                return new MoveResult("Checkmate");
            } else if (game.isInStalemate(nextTurn)) {
                return new MoveResult("Stalemate");
            } else if (game.isInCheck(nextTurn)) {
                return new MoveResult("Check");
            }

            return new MoveResult(); // Move successful, no special condition

        } catch (DataAccessException e) {
            return new MoveResult("Data error: " + e.getMessage());
        } catch (InvalidMoveException e) {
            return new MoveResult("Error: " + e.getMessage());
        }
    }


    public ResignResult resign(ResignRequest request) {
        try {
            AuthData auth = authDAO.getAuth(request.authToken());
            if (auth == null) {
                return new ResignResult("Error: unauthorized");
            }

            GameData gameData = gameDAO.getGame(request.gameID());
            if (gameData == null) {
                return new ResignResult("Error: game not found");
            }

            return new ResignResult("Player resigned."); // You could also mark game as over if you extend GameData.

        } catch (DataAccessException e) {
            return new ResignResult("Error: " + e.getMessage());
        }
    }

    public LeaveResult leave(LeaveRequest request) {
        try {
            AuthData auth = authDAO.getAuth(request.getAuthToken());
            if (auth == null) {
                return new LeaveResult("Error: unauthorized");
            }

            GameData gameData = gameDAO.getGame(request.getGameID());
            if (gameData == null) {
                return new LeaveResult("Error: game not found");
            }

            String username = auth.getUsername();
            String white = gameData.getWhiteUsername();
            String black = gameData.getBlackUsername();

            if (username.equals(gameData.getWhiteUsername())) {
                gameData = new GameData(
                        gameData.getGameID(),
                        gameData.getGameName(),
                        null,  // set white username to null
                        gameData.getBlackUsername(),
                        gameData.game());
            } else if (username.equals(gameData.getBlackUsername())) {
                gameData = new GameData(
                        gameData.getGameID(),
                        gameData.getGameName(),
                        gameData.getWhiteUsername(),
                        null,  // set black username to null
                        gameData.game());
            }
            gameDAO.updateGame(gameData);
            return new LeaveResult();

        } catch (DataAccessException e) {
            return new LeaveResult("Error: " + e.getMessage());
        }
    }


}
