package service;

import dataAccess.*;
import org.junit.jupiter.api.*;
import request.*;
import result.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestLoginService {

    private LoginService service;
    private UserService userService;

    @BeforeEach
    public void setup() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        service = new LoginService(userDAO, authDAO);
        userService = new UserService(userDAO, authDAO);
        userService.register(new RegisterRequest("wes", "pass", "w@e.com"));
    }

    @Test
    public void testLoginPositive() {
        LoginRequest request = new LoginRequest("wes", "pass");
        LoginResult result = service.login(request);
        assertNull(result.getMessage());
        assertNotNull(result.getAuthToken());
    }

    @Test
    public void testLoginNegative() {
        LoginRequest request = new LoginRequest("wes", "wrongpass");
        LoginResult result = service.login(request);
        assertNotNull(result.getMessage());
    }
}
