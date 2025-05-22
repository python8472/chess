package model;

/**
 * Represents an authentication token issued to a user.
 */
public class AuthData {
    private final String username;
    private final String authToken;

    public AuthData(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }

    public String getUsername() {
        return username;
    }

    public String getAuthToken() {
        return authToken;
    }
}
