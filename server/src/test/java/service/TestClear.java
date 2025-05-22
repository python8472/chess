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
        userDAO.createUser(new UserData("wes", "pass", "email"));
        authDAO.createAuth("wes");
        gameDAO.createGame("Cool Game");
    }

    @Test
    @DisplayName("Clear Wipes All Tables")
    void testClearAllMemoryDAOs() {
        // Sanity check before clearing
        assertNotNull(userDAO.getUser("wes"));
        assertNotNull(authDAO.getAuth(authDAO.getAllAuthTokens().iterator().next()));
        assertFalse(gameDAO.listGames().isEmpty());

        // Perform clear
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();

        // Post-clear assertions
        assertNull(userDAO.getUser("wes"));
        assertTrue(authDAO.getAllAuthTokens().isEmpty(), "Auth tokens not cleared");
        assertTrue(gameDAO.listGames().isEmpty(), "Games not cleared");
    }
}
