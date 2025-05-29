package dataaccess;

import dataaccess.sql.SQLGameDAO;
import dataaccess.sql.SQLUserDAO;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SQLGameDAOTest {
    SQLGameDAO gameDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        SQLGameDAO gameDAO = new SQLGameDAO();
        SQLUserDAO userDAO = new SQLUserDAO();

        // Clear tables before each test
        gameDAO.clear();
        userDAO.clear();

        // Create users to satisfy foreign key constraints
        userDAO.createUser(new UserData("nick", "pw", "nick@email.com"));
        userDAO.createUser(new UserData("ally", "pw", "ally@email.com"));
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        gameDAO = new SQLGameDAO();
        gameDAO.clear();
    }

    // --- createGame ---
    @Test
    @Order(1)
    public void createGamePositive() throws DataAccessException {
        int id = gameDAO.createGame("test game");
        assertTrue(id > 0);
    }

    @Test
    @Order(2)
    public void createGameNegative_EmptyName() {
        assertThrows(DataAccessException.class, () -> gameDAO.createGame(""));
    }

    // --- getGame ---
    @Test
    @Order(3)
    public void getGamePositive() throws DataAccessException {
        int id = gameDAO.createGame("look me up");
        GameData game = gameDAO.getGame(id);
        assertEquals("look me up", game.getGameName());
    }

    @Test
    @Order(4)
    public void getGameNegative_NotFound() throws DataAccessException {
        GameData game = gameDAO.getGame(-1);
        assertNull(game);
    }

    // --- updateGame ---
    @Test
    @Order(5)
    public void updateGamePositive() throws DataAccessException {
        gameDAO.createGame("update me");
        GameData game = gameDAO.listGames().getFirst();
        GameData updated = new GameData(game.getGameID(), game.getGameName(), "nick", null, gameDAO.getGame(game.getGameID()).game());
        gameDAO.updateGame(updated);
        GameData found = gameDAO.getGame(game.getGameID());
        assertEquals("nick", found.getWhiteUsername());
    }

    @Test
    @Order(6)
    public void updateGameNegative_InvalidUser() throws DataAccessException {
        gameDAO.createGame("update fail");
        GameData game = gameDAO.listGames().getFirst();
        GameData badUpdate = new GameData(game.getGameID(), game.getGameName(), "no_such_user", null,gameDAO.getGame(game.getGameID()).game());
        assertThrows(DataAccessException.class, () -> gameDAO.updateGame(badUpdate));
    }

    // --- listGames ---
    @Test
    @Order(7)
    public void listGamesPositive() throws DataAccessException {
        gameDAO.createGame("one");
        gameDAO.createGame("two");
        List<GameData> games = gameDAO.listGames();
        assertEquals(2, games.size());
    }

    @Test
    @Order(8)
    public void listGamesNegative_NoneExist() throws DataAccessException {
        List<GameData> games = gameDAO.listGames();
        assertEquals(0, games.size());
    }

    // --- clear ---
    @Test
    @Order(9)
    public void clearPositive() throws DataAccessException {
        gameDAO.createGame("clear me");
        gameDAO.clear();
        assertEquals(0, gameDAO.listGames().size());
    }

    @Test
    @Order(10)
    public void clearNegative_CalledTwice() throws DataAccessException {
        gameDAO.createGame("clear twice");
        gameDAO.clear();
        assertDoesNotThrow(() -> gameDAO.clear());
        assertEquals(0, gameDAO.listGames().size());
    }
}
