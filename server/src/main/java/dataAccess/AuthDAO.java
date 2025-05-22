package dataAccess;

import shared.model.AuthData;

public interface AuthDAO {
    /**
     * Creates a new auth token for a given username.
     * @param username the user to associate with the token
     * @return the generated AuthData object
     */
    AuthData createAuth(String username);
}
