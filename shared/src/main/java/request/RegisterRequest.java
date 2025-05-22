package request;

/**
 * Request body for user registration (POST /user).
 */
public class RegisterRequest {
    public String username;
    public String password;
    public String email;

    public RegisterRequest() {
        // Required for Gson deserialization
    }

    public RegisterRequest(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
