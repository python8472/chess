package result;

public class ClearResult {
    private final String message;

    public ClearResult() {
        this.message = "Clear succeeded.";
    }

    public ClearResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
