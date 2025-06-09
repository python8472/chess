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

        String sql = "INSERT INTO games (gameName, game) VALUES (?, ?)";
        String gameJson = gson.toJson(new ChessGame());

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
    public void updateGame(int i, GameData gameData) throws DataAccessException {
        String sql = "UPDATE games SET whitePlayer=?, blackPlayer=?, game=? WHERE id=?";
        String gameJson = gson.toJson(gameData.game());

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, gameData.getWhiteUsername());
            stmt.setString(2, gameData.getBlackUsername());
            stmt.setString(3, gameJson);
            stmt.setInt(4, gameData.getGameID());
            stmt.executeUpdate();

        } catch (SQLException | DataAccessException e) {
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
