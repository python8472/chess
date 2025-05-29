package dataaccess;

import dataaccess.sql.SQLUserDAO;
import model.UserData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SQLUserDAOTest {

    private static UserDAO userDAO;

    @BeforeAll
    static void setup() throws DataAccessException {
        DatabaseManager.configureDatabase();
        userDAO = new SQLUserDAO();
    }

    @BeforeEach
    void clearDB() throws DataAccessException {
        userDAO.clear();
    }

    @Test
    @Order(1)
    void createUserPositive() throws DataAccessException {
        UserData user = new UserData("testuser", "password", "test@example.com");
        userDAO.createUser(user);
        UserData result = userDAO.getUser("testuser");
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    @Order(2)
    void createUserNegative_Duplicate() throws DataAccessException {
        var user = new UserData("duplicate", "pass", "email");
        userDAO.createUser(user);
        assertThrows(DataAccessException.class, () -> userDAO.createUser(user));
    }

    @Test
    @Order(3)
    void getUserPositive() throws DataAccessException {
        UserData user = new UserData("getuser", "mypassword", "get@example.com");
        userDAO.createUser(user);
        UserData result = userDAO.getUser("getuser");
        assertNotNull(result);
        assertEquals("getuser", result.getUsername());
    }

    @Test
    @Order(4)
    void getUserNegative_NotFound() throws DataAccessException {
        UserData result = userDAO.getUser("nonexistent");
        assertNull(result);
    }

    @Test
    @Order(5)
    void clearPositive() throws DataAccessException {
        userDAO.createUser(new UserData("a", "a", "a@a.com"));
        userDAO.clear();
        assertNull(userDAO.getUser("a"));
    }

    @Test
    @Order(6)
    void clearNegative_Empty() {
        assertDoesNotThrow(userDAO::clear);
    }
}
