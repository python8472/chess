package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import result.LogoutResult;

public class LogoutService {
    private final AuthDAO authDAO;

    public LogoutService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public LogoutResult logout(String authToken) throws DataAccessException {
        if (authToken == null || authDAO.getAuth(authToken) == null) {
            return new LogoutResult("Error: unauthorized");
        }

        authDAO.deleteAuth(authToken);
        return new LogoutResult(); // Success: message is null
    }
}

