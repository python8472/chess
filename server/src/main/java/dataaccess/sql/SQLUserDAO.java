package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import dataaccess.DatabaseManager;
import org.mindrot.jbcrypt.BCrypt;

public class SQLUserDAO implements UserDAO {

    @Override
    public void createUser(UserData user) throws DataAccessException {
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, hashedPassword);
            stmt.setString(3, user.getEmail());
            stmt.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("SQLUserDAO.createUser failed", e);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UserData(username, rs.getString("password"), rs.getString("email"));
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("SQLUserDAO.getUser failed", e);
        }
        return null;
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM users";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("SQLUserDAO.clear failed", e);
        }
    }
}
