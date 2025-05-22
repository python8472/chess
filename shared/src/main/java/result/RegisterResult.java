package result;

/**
 * Response body for user registration (POST /user).
 * Returns either success (username + authToken) or an error message.
 */
public class RegisterResult {
    private String username;
    private String authToken;
    private String message;

    public RegisterResult(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }

    public RegisterResult(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getMessage() {
        return message;
    }
}
