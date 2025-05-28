package dataaccess;

import model.AuthData;

import java.util.Collection;

public interface AuthDAO {
    AuthData createAuth(String username) throws DataAccessException;     // POST /user, /session
    AuthData getAuth(String authToken) throws DataAccessException;       // used to validate auth
    void deleteAuth(String authToken) throws DataAccessException;        // DELETE /session
    void clear() throws DataAccessException;                             // DELETE /db
    Collection<String> getAllAuthTokens() throws DataAccessException;    // used internally for debugging/testing
}
