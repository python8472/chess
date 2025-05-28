package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;
import model.AuthData;
import request.RegisterRequest;
import result.RegisterResult;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest req) throws DataAccessException {
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
