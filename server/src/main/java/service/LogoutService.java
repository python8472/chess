package service;

import dataAccess.AuthDAO;
import result.LogoutResult;

public class LogoutService {
    private final AuthDAO authDAO;

    public LogoutService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public LogoutResult logout(String authToken) {
        if (authToken == null || authDAO.getAuth(authToken) == null) {
            return new LogoutResult("Error: unauthorized");
        }

        authDAO.deleteAuth(authToken);
        return new LogoutResult();  // success, null message
    }
}

