package dataaccess.sql;

import dataaccess.UserDAO;
import model.UserData;

public class SQLUserDAO implements UserDAO {

    @Override
    public void createUser(UserData user) {
        // TODO = SQL INSERT INTO users
    }

    @Override
    public UserData getUser(String username) {
        // TODO = SQL SELECT FROM users WHERE username = x
        return null;
    }

    @Override
    public void clear() {
        // TODO = Implement SQL DELETE FROM users
    }
}
