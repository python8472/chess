package passoff;

import facade.ServerFacade;
import org.junit.jupiter.api.*;
import request.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on port " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDatabase() {
        server.clear(); // delete all just for the testing
    }

    @Test
    public void registerPositive() throws Exception {
        var result = facade.register(new RegisterRequest("user1", "pass", "u1@x.com"));
        assertNull(result.getMessage());
        assertNotNull(result.getAuthToken());
    }

    @Test
    public void registerNegativeDuplicate() throws Exception {
        facade.register(new RegisterRequest("user1", "pass", "u1@x.com"));
        var result = facade.register(new RegisterRequest("user1", "pass", "u1@x.com"));
        assertNotNull(result.getMessage());
    }

    @Test
    public void loginPositive() throws Exception {
        facade.register(new RegisterRequest("user2", "pass", "u2@x.com"));
        var result = facade.login(new LoginRequest("user2", "pass"));
        assertNull(result.getMessage());
        assertNotNull(result.getAuthToken());
    }

    @Test
    public void loginNegativeWrongPassword() throws Exception {
        facade.register(new RegisterRequest("user3", "pass", "u3@x.com"));
        var result = facade.login(new LoginRequest("user3", "wrongpass"));
        assertNotNull(result.getMessage());
    }

    @Test
    public void logoutPositive() throws Exception {
        var reg = facade.register(new RegisterRequest("user4", "pass", "u4@x.com"));
        var result = facade.logout(new LogoutRequest(reg.getAuthToken()), reg.getAuthToken());
        assertNull(result.getMessage());
    }

    @Test
    public void logoutNegativeBadToken() throws Exception {
        var result = facade.logout(new LogoutRequest("badToken"), "badToken");
        assertNotNull(result.getMessage());
    }

    @Test
    public void listGamesPositive() throws Exception {
        var reg = facade.register(new RegisterRequest("user5", "pass", "u5@x.com"));
        var result = facade.listGames(reg.getAuthToken());
        assertNull(result.getMessage());
        assertNotNull(result.getGames());
    }

    @Test
    public void listGamesNegativeBadAuth() throws Exception {
        var result = facade.listGames("invalidToken");
        assertNotNull(result.getMessage());
    }

    @Test
    public void createGamePositive() throws Exception {
        var reg = facade.register(new RegisterRequest("user6", "pass", "u6@x.com"));
        var result = facade.createGame(new CreateGameRequest("game6"), reg.getAuthToken());
        assertNull(result.getMessage());
        assertTrue(result.getGameID() > 0);
    }

    @Test
    public void createGameNegativeBadAuth() throws Exception {
        var result = facade.createGame(new CreateGameRequest("badGame"), "invalidToken");
        assertNotNull(result.getMessage());
    }

    @Test
    public void joinGamePositive() throws Exception {
        var reg = facade.register(new RegisterRequest("user7", "pass", "u7@x.com"));
        var created = facade.createGame(new CreateGameRequest("game7"), reg.getAuthToken());
        var result = facade.joinGame(new JoinGameRequest("WHITE", created.getGameID()), reg.getAuthToken());
        assertNull(result.getMessage());
    }

    @Test
    public void joinGameNegativeBadColor() throws Exception {
        var reg = facade.register(new RegisterRequest("user8", "pass", "u8@x.com"));
        var created = facade.createGame(new CreateGameRequest("game8"), reg.getAuthToken());
        var result = facade.joinGame(new JoinGameRequest("MURPLE", created.getGameID()), reg.getAuthToken());
        assertNotNull(result.getMessage());
    }
}
