package result;

public class ClearResult {
    private String message;

    public ClearResult() {
        this.message = null;
    }

    public ClearResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
