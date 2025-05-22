package dataAccess;

import shared.model.UserData;

public interface UserDAO {
    UserData getUser(String username);
    void createUser(UserData user);
}
