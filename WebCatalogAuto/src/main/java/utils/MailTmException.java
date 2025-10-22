package utils;

public class MailTmException extends RuntimeException {
    public MailTmException(String message) {
        super(message);
    }

    public MailTmException(String message, Throwable cause) {
        super(message, cause);
    }
}
