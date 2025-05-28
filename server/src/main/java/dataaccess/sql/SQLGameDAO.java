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
    public int createGame(String gameName) {
        String sql = "INSERT INTO games (gameName, game) VALUES (?, ?)";
        String gameJson = gson.toJson(new ChessGame());

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, gameName);
            stmt.setString(2, gameJson);
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        } catch (DataAccessException | SQLException e) {
            System.err.println("createGame: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public GameData getGame(int gameID) {
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
            }
        } catch (DataAccessException | SQLException e) {
            System.err.println("getGame: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void updateGame(GameData gameData) {
        String sql = "UPDATE games SET whitePlayer=?, blackPlayer=?, game=? WHERE id=?";
        String gameJson = gson.toJson(gameData.game());

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, gameData.getWhiteUsername());
            stmt.setString(2, gameData.getBlackUsername());
            stmt.setString(3, gameJson);
            stmt.setInt(4, gameData.getGameID());
            stmt.executeUpdate();
        } catch (DataAccessException | SQLException e) {
            System.err.println("updateGame: " + e.getMessage());
        }
    }

    @Override
    public List<GameData> listGames() {
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
                        null // we skip full ChessGame deserialization for listing
                ));
            }
        } catch (DataAccessException | SQLException e) {
            System.err.println("listGames: " + e.getMessage());
        }
        return games;
    }

    @Override
    public void clear() {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM games")) {
            stmt.executeUpdate();
        } catch (DataAccessException | SQLException e) {
            System.err.println("clear: " + e.getMessage());
        }
    }
}
