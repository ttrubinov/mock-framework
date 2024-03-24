package mock.exception;

public class MockException extends RuntimeException {
    public MockException() {
    }

    public MockException(String message) {
        super(message);
    }

    public MockException(String message, Throwable cause) {
        super(message, cause);
    }
}
