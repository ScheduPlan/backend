package de.hofuniversity.assemblyplanner.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ResourceNotFoundException extends ResponseStatusException {
    public static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;
    public static final int RAW_STATUS_CODE = 404;

    public ResourceNotFoundException() {
        super(HTTP_STATUS);
    }

    public ResourceNotFoundException(String reason) {
        super(HTTP_STATUS, reason);
    }

    public ResourceNotFoundException(String reason, Throwable cause) {
        super(RAW_STATUS_CODE, reason, cause);
    }

    protected ResourceNotFoundException(String reason, Throwable cause, String messageDetailCode, Object[] messageDetailArguments) {
        super(HTTP_STATUS, reason, cause, messageDetailCode, messageDetailArguments);
    }
}
