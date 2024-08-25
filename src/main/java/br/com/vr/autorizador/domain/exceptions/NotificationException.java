package br.com.vr.autorizador.domain.exceptions;

import br.com.vr.autorizador.domain.validation.Error;
import br.com.vr.autorizador.domain.validation.handler.NotificationHandler;

import java.util.List;

public class NotificationException extends NoStacktraceException implements ExceptionHandler {

    private List<Error> errors;

    private NotificationException(String message, List<Error> errors) {
        super(message);
        this.errors = errors;
    }

    public NotificationException(String message) {
        this(message, List.of(new Error(message)));
    }

    public static NotificationException with(String message, NotificationHandler notification) {
        return new NotificationException(message, notification.getErrors());
    }

    @Override
    public int numberOfErrors() {
        return this.errors.size();
    }

    @Override
    public Error firstError() {
        return this.errors.get(0);
    }
}
