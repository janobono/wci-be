package sk.janobono.wci.common.exception;

import java.text.MessageFormat;

public enum ApplicationExceptionCode {
    UNKNOWN,
    INVALID_CAPTCHA,
    INVALID_CREDENTIALS,
    USER_IS_DISABLED,
    USER_NOT_FOUND,
    USER_USERNAME_IS_USED,
    USER_EMAIL_IS_USED,
    GDPR,
    UNSUPPORTED_LOCALE;

    public ApplicationException exception(String pattern, Object... arguments) {
        return exception(null, pattern, arguments);
    }

    public ApplicationException exception(Throwable cause, String pattern, Object... arguments) {
        return new ApplicationException(MessageFormat.format(pattern, arguments), cause, this);
    }
}
