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
                return null;
            }

            GameData gameData = gameDAO.getGame(request.gameID());
            if (gameData == null) {
                return null;
            }

            ChessGame game = gameData.game();

            if (game.getTeamTurn() != request.playerColor()) {
                return null;
            }

            Collection<ChessMove> legalMoves = game.validMoves(request.move().getStartPosition());
            boolean isLegal = legalMoves.stream().anyMatch(move -> move.equals(request.move()));
            if (!isLegal) {
                return null;
            }

            game.makeMove(request.move());
            gameDAO.updateGame(gameData);

            // Detect endgame conditions
            ChessGame.TeamColor nextTurn = game.getTeamTurn();
            boolean inCheck = game.isInCheck(nextTurn);
            boolean noMoves = GameHelper.hasNoLegalMoves(game.getBoard(), nextTurn);

            if (inCheck && noMoves) {
            } else if (!inCheck && noMoves) {
            }

        } catch (DataAccessException | InvalidMoveException ignored) {
        }
        return null;
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
