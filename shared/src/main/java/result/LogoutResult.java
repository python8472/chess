package result;

public class LogoutResult {
    private String message;

    public LogoutResult() {
        this.message = null;
    }

    public LogoutResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
