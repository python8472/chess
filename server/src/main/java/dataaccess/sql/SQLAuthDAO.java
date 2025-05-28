package dataaccess.sql;

import dataaccess.AuthDAO;
import model.AuthData;

import java.util.Collection;
import java.util.Collections;

public class SQLAuthDAO implements AuthDAO {

    @Override
    public AuthData createAuth(String username) {
        // TODO = Generate token, store (token, username), and return AuthData
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) {
        // TODO = SELECT * FROM auth_tokens WHERE token = ?
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {
        // TODO = DELETE FROM auth_tokens WHERE token = ?
    }

    @Override
    public void clear() {
        // TODO = DELETE FROM auth_tokens
    }

    @Override
    public Collection<String> getAllAuthTokens() {
        // TODO = SELECT token FROM auth_tokens
        return Collections.emptyList();
    }
}
