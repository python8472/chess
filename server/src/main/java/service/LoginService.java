package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import result.LoginResult;
import org.mindrot.jbcrypt.BCrypt;

public class LoginService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public LoginService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public LoginResult login(LoginRequest req) {
        if (req.getUsername() == null || req.getPassword() == null) {
            return new LoginResult("Error: missing fields"); // 400
        }

        UserData user = userDAO.getUser(req.getUsername());
        if (user == null || !BCrypt.checkpw(req.getPassword(), user.getPassword())) {
            return new LoginResult("Error: unauthorized"); // 401
        }

        AuthData auth = authDAO.createAuth(req.getUsername());
        return new LoginResult(auth.getUsername(), auth.getAuthToken()); // 200
    }
}
