package dataaccess.sql;

import dataaccess.AuthDAO;
import dataaccess.DatabaseManager;
import dataaccess.DataAccessException;
import model.AuthData;

import java.sql.*;
import java.util.*;

public class SQLAuthDAO implements AuthDAO {

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        String token = UUID.randomUUID().toString();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO auth_tokens (token, username) VALUES (?, ?)")) {
            stmt.setString(1, token);
            stmt.setString(2, username);
            stmt.executeUpdate();
            return new AuthData(token, username);
        } catch (SQLException e) {
            throw new DataAccessException("createAuth failed", e);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT username FROM auth_tokens WHERE token = ?")) {
            stmt.setString(1, authToken);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? new AuthData(authToken, rs.getString("username")) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("getAuth failed", e);
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM auth_tokens WHERE token = ?")) {
            stmt.setString(1, authToken);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("deleteAuth failed", e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM auth_tokens")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("clear failed", e);
        }
    }

    @Override
    public Collection<String> getAllAuthTokens() throws DataAccessException {
        Set<String> tokens = new HashSet<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT token FROM auth_tokens");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) tokens.add(rs.getString("token"));
        } catch (SQLException e) {
            throw new DataAccessException("getAllAuthTokens failed", e);
        }
        return tokens;
    }
}
