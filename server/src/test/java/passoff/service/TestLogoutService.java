package passoff.service;

import dataAccess.*;
import org.junit.jupiter.api.*;
import result.LogoutResult;
import model.AuthData;
import service.LogoutService;

import static org.junit.jupiter.api.Assertions.*;

public class TestLogoutService {

    private LogoutService service;
    private AuthDAO authDAO;

    @BeforeEach
    public void setup() {
        authDAO = new MemoryAuthDAO();
        service = new LogoutService(authDAO);
    }

    @Test
    public void testLogoutPositive() {
        AuthData auth = authDAO.createAuth("nick_j");
        LogoutResult result = service.logout(auth.getAuthToken());
        assertNull(result.getMessage());
    }

    @Test
    public void testLogoutNegative() {
        LogoutResult result = service.logout("invalid-token");
        assertNotNull(result.getMessage());
    }
}
