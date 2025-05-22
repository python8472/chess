package dataAccess;

import model.AuthData;

public interface AuthDAO {
    AuthData createAuth(String username);             // POST /user, /session
    AuthData getAuth(String authToken);               // used to validate auth
    void deleteAuth(String authToken);                // DELETE /session
    void clear();                                     // DELETE /db
}
