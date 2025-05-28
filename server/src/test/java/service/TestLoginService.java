package service;

import dataaccess.*;
import dataaccess.sql.SQLAuthDAO;
import dataaccess.sql.SQLUserDAO;
import org.junit.jupiter.api.*;
import request.*;
import result.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestLoginService {

    private LoginService loginService;

    @BeforeEach
    public void setup() throws DataAccessException {
        // Use SQL DAOs to match BCrypt login expectations
        AuthDAO authDAO = new SQLAuthDAO();
        UserDAO userDAO = new SQLUserDAO();

        // Clear database
        authDAO.clear();
        userDAO.clear();

        // Initialize services
        UserService userService = new UserService(userDAO, authDAO);
        loginService = new LoginService(userDAO, authDAO);

        // Register user using actual registration logic (which hashes passwords)
        RegisterRequest req = new RegisterRequest("nick_j", "pw", "woof.com");
        RegisterResult regResult = userService.register(req);
        assertNotNull(regResult.getAuthToken(), "Registration failed in setup");
    }

    @Test
    public void testLoginPositive() throws DataAccessException {
        LoginRequest request = new LoginRequest("nick_j", "pw");
        LoginResult result = loginService.login(request);

        assertNull(result.getMessage(), "Login should succeed");
        assertNotNull(result.getAuthToken(), "Auth token should be returned");
    }

    @Test
    public void testLoginNegative() throws DataAccessException {
        LoginRequest request = new LoginRequest("nick_j", "wrong_pw");
        LoginResult result = loginService.login(request);

        assertNotNull(result.getMessage(), "Login should fail with wrong password");
        assertTrue(result.getMessage().toLowerCase().contains("error"), "Message should contain 'error'");
    }
}
