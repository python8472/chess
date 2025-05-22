package dataAccess;

import model.AuthData;

import java.util.Collection;

public interface AuthDAO {
    AuthData createAuth(String username);             // POST /user, /session
    AuthData getAuth(String authToken);               // used to validate auth
    void deleteAuth(String authToken);                // DELETE /session
    void clear();
    Collection<String> getAllAuthTokens();// DELETE /db
}
