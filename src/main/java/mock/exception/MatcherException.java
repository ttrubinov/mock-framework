package mock.exception;

public class MatcherException extends MockException {
    public MatcherException() {
    }

    public MatcherException(String message) {
        super(message);
    }

    public MatcherException(String message, Throwable cause) {
        super(message, cause);
    }
}
