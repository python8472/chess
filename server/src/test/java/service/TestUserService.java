package service;

import dataaccess.*;
import org.junit.jupiter.api.*;
import request.RegisterRequest;
import result.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class TestUserService {

    private UserService service;

    @BeforeEach
    public void setup() {
        service = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());
    }

    @Test
    public void testRegisterPositive() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("nick_j", "pw", "woof.com");
        RegisterResult result = service.register(request);
        assertNull(result.getMessage());
        assertNotNull(result.getAuthToken());
    }

    @Test
    public void testRegisterNegative() throws DataAccessException {
        RegisterRequest request = new RegisterRequest(null, "nah", "woof.com");
        RegisterResult result = service.register(request);
        assertNotNull(result.getMessage());
    }
}
