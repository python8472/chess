package model;

/**
 * Represents a user in the system, including their username, password, and email.
 */
public class UserData {
    private final String username;
    private final String password;
    private final String email;

    public UserData(String username, String password, String email) {
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
