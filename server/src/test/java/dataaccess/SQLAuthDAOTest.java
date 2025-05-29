package dataaccess;

import dataaccess.sql.SQLAuthDAO;
import dataaccess.sql.SQLUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class SQLAuthDAOTest {

    private AuthDAO authDAO;
    private UserDAO userDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        authDAO = new SQLAuthDAO();
        userDAO = new SQLUserDAO();
        authDAO.clear();
        userDAO.clear();
    }

    @Test
    @DisplayName("Create Auth - Positive")
    void createAuthPositive() throws DataAccessException {
        userDAO.createUser(new UserData("testUser", "pw", "test@example.com"));
        AuthData auth = authDAO.createAuth("testUser");
        assertNotNull(auth);
        assertEquals("testUser", auth.getUsername());
        assertNotNull(auth.getAuthToken());
    }

    @Test
    @DisplayName("Create Auth - Negative (Empty Username)")
    void createAuthNegative() {
        assertThrows(DataAccessException.class, () -> authDAO.createAuth(""));
    }

    @Test
    @DisplayName("Get Auth - Positive")
    void getAuthPositive() throws DataAccessException {
        userDAO.createUser(new UserData("getUser", "pw", "test@example.com"));
        AuthData created = authDAO.createAuth("getUser");
        AuthData fetched = authDAO.getAuth(created.getAuthToken());
        assertEquals(created, fetched);
    }

    @Test
    @DisplayName("Get Auth - Negative (Non-existent Token)")
    void getAuthNegative() throws DataAccessException {
        AuthData fetched = authDAO.getAuth("nonexistent-token");
        assertNull(fetched);
    }

    @Test
    @DisplayName("Delete Auth - Positive")
    void deleteAuthPositive() throws DataAccessException {
        userDAO.createUser(new UserData("deleteUser", "pw", "test@example.com"));
        AuthData created = authDAO.createAuth("deleteUser");
        authDAO.deleteAuth(created.getAuthToken());
        assertNull(authDAO.getAuth(created.getAuthToken()));
    }

    @Test
    @DisplayName("Delete Auth - Negative (Non-existent Token)")
    void deleteAuthNegative() {
        assertDoesNotThrow(() -> authDAO.deleteAuth("nonexistent-token"));
    }

    @Test
    @DisplayName("Clear Auths - Positive")
    void clearPositive() throws DataAccessException {
        userDAO.createUser(new UserData("user1", "pw", "email1@example.com"));
        userDAO.createUser(new UserData("user2", "pw", "email2@example.com"));
        authDAO.createAuth("user1");
        authDAO.createAuth("user2");
        authDAO.clear();
        assertTrue(authDAO.getAllAuthTokens().isEmpty());
    }

    @Test
    @DisplayName("Clear Auths - Negative (Redundant Clear)")
    void clearNegative() {
        assertDoesNotThrow(() -> authDAO.clear());
    }

    @Test
    @DisplayName("Get All Auth Tokens - Positive")
    void getAllAuthTokensPositive() throws DataAccessException {
        userDAO.createUser(new UserData("u1", "pw", "1@example.com"));
        userDAO.createUser(new UserData("u2", "pw", "2@example.com"));
        AuthData a1 = authDAO.createAuth("u1");
        AuthData a2 = authDAO.createAuth("u2");
        Collection<String> tokens = authDAO.getAllAuthTokens();
        assertEquals(2, tokens.size());
        assertTrue(tokens.contains(a1.getAuthToken()));
        assertTrue(tokens.contains(a2.getAuthToken()));
    }

    @Test
    @DisplayName("Get All Auth Tokens - Negative (Empty List)")
    void getAllAuthTokensNegative() {
        assertDoesNotThrow(() -> {
            Collection<String> tokens = authDAO.getAllAuthTokens();
            assertNotNull(tokens);
        });
    }
}
