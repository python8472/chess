package dataaccess;

import dataaccess.sql.SQLAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.*;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class SQLAuthDAOTest {

    private AuthDAO authDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        authDAO = new SQLAuthDAO();
        authDAO.clear();
    }

    @Test
    void createAuthPositive() throws DataAccessException {
        AuthData auth = authDAO.createAuth("testUser");
        assertNotNull(auth);
        assertEquals("testUser", auth.getUsername());
        assertNotNull(auth.getAuthToken());
    }

    @Test
    void createAuthNegative() {
        assertDoesNotThrow(() -> {
            AuthData auth = authDAO.createAuth("");
            assertNotNull(auth);
        });
    }

    @Test
    void getAuthPositive() throws DataAccessException {
        AuthData created = authDAO.createAuth("getUser");
        AuthData fetched = authDAO.getAuth(created.getAuthToken());
        assertEquals(created, fetched);
    }

    @Test
    void getAuthNegative() throws DataAccessException {
        AuthData fetched = authDAO.getAuth("nonexistent-token");
        assertNull(fetched);
    }

    @Test
    void deleteAuthPositive() throws DataAccessException {
        AuthData created = authDAO.createAuth("deleteUser");
        authDAO.deleteAuth(created.getAuthToken());
        assertNull(authDAO.getAuth(created.getAuthToken()));
    }

    @Test
    void deleteAuthNegative() {
        assertDoesNotThrow(() -> authDAO.deleteAuth("nonexistent-token"));
    }

    @Test
    void clearPositive() throws DataAccessException {
        authDAO.createAuth("user1");
        authDAO.createAuth("user2");
        authDAO.clear();
        assertTrue(authDAO.getAllAuthTokens().isEmpty());
    }

    @Test
    void clearNegative() {
        assertDoesNotThrow(() -> authDAO.clear());
    }

    @Test
    void getAllAuthTokensPositive() throws DataAccessException {
        authDAO.createAuth("u1");
        authDAO.createAuth("u2");
        Collection<String> tokens = authDAO.getAllAuthTokens();
        assertEquals(2, tokens.size());
    }

    @Test
    void getAllAuthTokensNegative() {
        assertDoesNotThrow(() -> {
            Collection<String> tokens = authDAO.getAllAuthTokens();
            assertNotNull(tokens);
        });
    }
}
