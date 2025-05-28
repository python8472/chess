package dataaccess.sql;

import dataaccess.AuthDAO;
import dataaccess.DatabaseManager;
import dataaccess.DataAccessException;
import model.AuthData;

import java.sql.*;
import java.util.*;

public class SQLAuthDAO implements AuthDAO {

    @Override
    public AuthData createAuth(String username) {
        String token = UUID.randomUUID().toString();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO auth_tokens (token, username) VALUES (?, ?)")) {
            stmt.setString(1, token);
            stmt.setString(2, username);
            stmt.executeUpdate();
            return new AuthData(token, username);
        } catch (DataAccessException | SQLException e) {
            System.err.println("createAuth: " + e.getMessage());
            return null;
        }
    }

    @Override
    public AuthData getAuth(String authToken) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT username FROM auth_tokens WHERE token = ?")) {
            stmt.setString(1, authToken);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? new AuthData(authToken, rs.getString("username")) : null;
            }
        } catch (DataAccessException | SQLException e) {
            System.err.println("getAuth: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void deleteAuth(String authToken) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM auth_tokens WHERE token = ?")) {
            stmt.setString(1, authToken);
            stmt.executeUpdate();
        } catch (DataAccessException | SQLException e) {
            System.err.println("deleteAuth: " + e.getMessage());
        }
    }

    @Override
    public void clear() {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM auth_tokens")) {
            stmt.executeUpdate();
        } catch (DataAccessException | SQLException e) {
            System.err.println("clear: " + e.getMessage());
        }
    }

    @Override
    public Collection<String> getAllAuthTokens() {
        Set<String> tokens = new HashSet<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT token FROM auth_tokens");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) tokens.add(rs.getString("token"));
        } catch (DataAccessException | SQLException e) {
            System.err.println("getAllAuthTokens: " + e.getMessage());
        }
        return tokens;
    }
}
