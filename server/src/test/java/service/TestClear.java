package service;

import dataAccess.*;
import model.*;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestClear {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    @BeforeEach
    void setUp() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

        // Simulate stored data
        userDAO.createUser(new UserData("nick_j", "pw", "woof.com"));
        authDAO.createAuth("nick_j");
        gameDAO.createGame("Cool Game");
    }

    @Test
    @DisplayName("Clear Wipes All Tables")
    void testClearAllMemoryDAOs() {
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
}
