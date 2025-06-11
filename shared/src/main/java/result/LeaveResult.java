package result;

public class LeaveResult {
    private final String message;

    public LeaveResult() {
        this.message = null;
    }

    public LeaveResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
