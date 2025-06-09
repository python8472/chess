package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import request.*;
import result.*;

import java.util.Collection;

public class GameplayService {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameplayService() {
        var db = DatabaseManager.getInstance();
        this.gameDAO = db.getGameDAO();
        this.authDAO = db.getAuthDAO();
    }

    public MoveResult makeMove(MoveRequest request) {
        try {
            AuthData auth = authDAO.getAuth(request.authToken());
            if (auth == null) return new MoveResult("Error: unauthorized");

            GameData gameData = gameDAO.getGame(request.gameID());
            if (gameData == null) return new MoveResult("Error: game not found");

            ChessGame game = gameData.getGame();
            if (game.getTeamTurn() != request.playerColor()) {
                return new MoveResult("Error: not your turn");
            }

            Collection<ChessMove> legalMoves = game.validMoves(request.move().getStartPosition());
            if (!legalMoves.contains(request.move())) {
                return new MoveResult("Error: illegal move");
            }

            game.makeMove(request.move());
            gameDAO.updateGame(request.gameID(), gameData);

            return new MoveResult(); // success
        } catch (DataAccessException e) {
            return new MoveResult("Error: " + e.getMessage());
        }
    }

    public ResignResult resign(ResignRequest request) {
        try {
            AuthData auth = authDAO.getAuth(request.authToken());
            if (auth == null) return new ResignResult("Error: unauthorized");

            GameData gameData = gameDAO.getGame(request.gameID());
            if (gameData == null) return new ResignResult("Error: game not found");

            return new ResignResult(); // success
        } catch (DataAccessException e) {
            return new ResignResult("Error: " + e.getMessage());
        }
    }

    public LeaveResult leave(LeaveRequest request) {
        try {
            AuthData auth = authDAO.getAuth(request.authToken());
            if (auth == null) return new LeaveResult("Error: unauthorized");

            GameData gameData = gameDAO.getGame(request.gameID());
            if (gameData == null) return new LeaveResult("Error: game not found");

            return new LeaveResult(); // success
        } catch (DataAccessException e) {
            return new LeaveResult("Error: " + e.getMessage());
        }
    }
}
