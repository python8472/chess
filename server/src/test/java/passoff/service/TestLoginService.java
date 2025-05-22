package passoff.service;

import dataAccess.*;
import org.junit.jupiter.api.*;
import request.*;
import result.*;
import service.LoginService;
import service.UserService;

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
        userService.register(new RegisterRequest("nick_j", "pw", "woof.com"));
    }

    @Test
    public void testLoginPositive() {
        LoginRequest request = new LoginRequest("nick_j", "pw");
        LoginResult result = service.login(request);
        assertNull(result.getMessage());
        assertNotNull(result.getAuthToken());
    }

    @Test
    public void testLoginNegative() {
        LoginRequest request = new LoginRequest("nick_j", "NAH");
        LoginResult result = service.login(request);
        assertNotNull(result.getMessage());
    }
}
