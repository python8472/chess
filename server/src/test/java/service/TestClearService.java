package service;
import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestClearService {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    @BeforeEach
    public void setup() throws DataAccessException {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

        // Simulate stored data
        userDAO.createUser(new UserData("nick_j", "pw", "woof.com"));
        authDAO.createAuth("nick_j");
        gameDAO.createGame("Cool Game");
    }

    @Test
    public void testClearPositive() throws DataAccessException {
        // Sanity check before clearing
        assertNotNull(userDAO.getUser("nick_j"));
        assertNotNull(authDAO.getAuth(authDAO.getAllAuthTokens().iterator().next()));
        assertFalse(gameDAO.listGames().isEmpty());

        // Perform clear
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();

        // Post-clear assertions
        assertNull(userDAO.getUser("nick_j"));
        assertTrue(authDAO.getAllAuthTokens().isEmpty(), "Auth tokens not cleared");
        assertTrue(gameDAO.listGames().isEmpty(), "Games not cleared");
    }

    @Test
    public void testClearNegative() throws DataAccessException {
        userDAO.createUser(new UserData("nope", "p", "e"));
        assertNotNull(userDAO.getUser("nope"));
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
        assertNull(userDAO.getUser("nope"));
    }
}