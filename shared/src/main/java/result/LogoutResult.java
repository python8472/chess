package result;

public class LogoutResult {
    private String message; // null if successful

    public LogoutResult() {
    }

    public LogoutResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return message == null;
    }
}
