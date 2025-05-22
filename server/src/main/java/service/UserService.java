package service;

import dataAccess.AuthDAO;
import dataAccess.UserDAO;
import shared.model.UserData;
import shared.model.AuthData;
import shared.request.RegisterRequest;
import shared.result.RegisterResult;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest req) {
        if (req.username == null || req.password == null || req.email == null) {
            return new RegisterResult("Error: missing required fields");
        }

        if (userDAO.getUser(req.username) != null) {
            return new RegisterResult("Error: username already taken");
        }

        UserData user = new UserData(req.username, req.password, req.email);
        userDAO.createUser(user);
        AuthData auth = authDAO.createAuth(user.getUsername());

        return new RegisterResult(auth.getUsername(), auth.getAuthToken());
    }
}
