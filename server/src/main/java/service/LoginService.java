package service;

import dataAccess.AuthDAO;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import result.LoginResult;

public class LoginService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public LoginService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public LoginResult login(LoginRequest req) {
        String username = req.getUsername();
        String password = req.getPassword();

        if (username == null || password == null) {
            return new LoginResult("Error: missing fields");
        }

        UserData user = userDAO.getUser(username);
        if (user == null || !user.getPassword().equals(password)) {
            return new LoginResult("Error: unauthorized");
        }

        AuthData auth = authDAO.createAuth(user.getUsername());
        return new LoginResult(auth.getUsername(), auth.getAuthToken());
    }
}
