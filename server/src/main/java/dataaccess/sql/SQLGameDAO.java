package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DatabaseManager;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLGameDAO implements GameDAO {
    private final Gson gson = new Gson();

    @Override
    public int createGame(String gameName) throws DataAccessException {
        if (gameName == null || gameName.trim().isEmpty()) {
            throw new DataAccessException("Game name cannot be null or empty");
        }

        ChessGame newGame = new ChessGame();
        newGame.getBoard().resetBoard();
        newGame.setTeamTurn(ChessGame.TeamColor.WHITE);
        String gameJson = gson.toJson(newGame);

        String sql = "INSERT INTO games (gameName, game) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, gameName);
            stmt.setString(2, gameJson);
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                } else {
                    throw new DataAccessException("Failed to retrieve game ID after insert");
                }
            }

        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("createGame failed", e);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT * FROM games WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ChessGame game = gson.fromJson(rs.getString("game"), ChessGame.class);
                    System.out.println("[DEBUG] Deserialized game: " + game);
                    System.out.println("[DEBUG] Deserialized board: " + game.getBoard());

                    return new GameData(
                            gameID,
                            rs.getString("gameName"),
                            rs.getString("whitePlayer"),
                            rs.getString("blackPlayer"),
                            game
                    );
                }
                return null;
            }

        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("getGame failed", e);
        }

    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        String sql = "UPDATE games SET whitePlayer=?, blackPlayer=?, game=? WHERE id=?";
        String gameJson = gson.toJson(gameData.game());
        System.out.println("[DEBUG] Saved game: " + gson.toJson(gameData.game()));

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (gameData.getWhiteUsername() != null && !gameData.getWhiteUsername().isBlank()) {
                stmt.setString(1, gameData.getWhiteUsername());
            } else {
                stmt.setNull(1, Types.VARCHAR);
            }

            if (gameData.getBlackUsername() != null && !gameData.getBlackUsername().isBlank()) {
                stmt.setString(2, gameData.getBlackUsername());
            } else {
                stmt.setNull(2, Types.VARCHAR);
            }

            stmt.setString(3, gameJson);
            stmt.setInt(4, gameData.getGameID());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("updateGame failed", e);
        }
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        List<GameData> games = new ArrayList<>();
        String sql = "SELECT * FROM games";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                games.add(new GameData(
                        rs.getInt("id"),
                        rs.getString("gameName"),
                        rs.getString("whitePlayer"),
                        rs.getString("blackPlayer"),
                        null // omit full ChessGame in list view
                ));
            }

        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("listGames failed", e);
        }

        return games;
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM games";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();

        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("clear failed", e);
        }
    }
}
