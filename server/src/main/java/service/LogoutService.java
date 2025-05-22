package service;

import dataAccess.AuthDAO;
import result.LogoutResult;

public class LogoutService {
    private final AuthDAO authDAO;

    public LogoutService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public LogoutResult logout(String authToken) {
        if (authToken == null || authToken.isEmpty()) {
            return new LogoutResult("Error: missing auth token");
        }

        authDAO.deleteAuth(authToken);
        return new LogoutResult(); // Success
    }
}
