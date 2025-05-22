package dataAccess;

import shared.model.AuthData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * In-memory implementation of AuthDAO.
 * Stores auth tokens and their associated usernames.
 */
public class MemoryAuthDAO implements AuthDAO {
    private final Map<String, AuthData> tokens = new HashMap<>();

    @Override
    public AuthData createAuth(String username) {
        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(username, token);
        tokens.put(token, auth);
        return auth;
    }

    // Optional: Add later if needed (e.g., for validation or logout)
    public AuthData getAuth(String token) {
        return tokens.get(token);
    }

    public void deleteAuth(String token) {
        tokens.remove(token);
    }

    public void clear() {
        tokens.clear();
    }
}
