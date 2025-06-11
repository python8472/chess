package service;
import dataaccess.*;
import model.GameData;
import org.junit.jupiter.api.*;
import request.*;
import result.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestGameService {

    private GameService service;
    private String token;

    @BeforeEach
    public void setup() throws DataAccessException {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        token = authDAO.createAuth("wes").getAuthToken();
        service = new GameService(gameDAO, authDAO);
    }

    @Test
    public void testCreateGamePositive() throws DataAccessException {
        CreateGameRequest request = new CreateGameRequest("Cool Game");
        CreateGameResult result = service.createGame(token, request);
        assertNull(result.getMessage());
        assertNotNull(result.getGameID());
    }

    @Test
    public void testCreateGameNegative() throws DataAccessException {
        CreateGameRequest request = new CreateGameRequest("");
        CreateGameResult result = service.createGame(token, request);
        assertNotNull(result.getMessage());
    }

    @Test
    public void testListGamesPositive() throws DataAccessException {
        ListGamesResult result = service.listGames(token);
        assertNull(result.getMessage());
        assertNotNull(result.getGames());
    }

    @Test
    public void testListGamesNegative() throws DataAccessException {
        ListGamesResult result = service.listGames("bad-token");
        assertNotNull(result.getMessage());
    }

    @Test
    public void testJoinGamePositive() throws DataAccessException {
        int validGameID = service.createGame(token, new CreateGameRequest("Joinable Game")).getGameID();
        JoinGameRequest request = new JoinGameRequest("WHITE", validGameID);
        JoinGameResult result = service.joinGame(token, request);
        assertNull(result.getMessage());
    }

    @Test
    public void testJoinGameNegative() throws DataAccessException {
        JoinGameRequest request = new JoinGameRequest("WHITE", 999);
        JoinGameResult result = service.joinGame(token, request);
        assertNotNull(result.getMessage());
    }
}