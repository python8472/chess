package dataAccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    private final Map<String, AuthData> tokens = new HashMap<>();

    @Override
    public AuthData createAuth(String username) {
        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(username, token);
        tokens.put(token, auth);
        return auth;
    }

    @Override
    public AuthData getAuth(String authToken) {
        return tokens.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        tokens.remove(authToken);
    }

    @Override
    public void clear() {
        tokens.clear();
    }
}
