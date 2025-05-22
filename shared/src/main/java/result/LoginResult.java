package result;

public class LoginResult {
    private String username;
    private String authToken;
    private String message;

    public LoginResult(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }

    public LoginResult(String message) {
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
