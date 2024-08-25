package br.com.vr.autorizador.domain.validation.handler;

import br.com.vr.autorizador.domain.validation.Error;
import br.com.vr.autorizador.domain.validation.ValidationHandler;

import java.util.ArrayList;
import java.util.List;

public class NotificationHandler implements ValidationHandler {

    private List<Error> errors;

    private NotificationHandler() {
        errors = new ArrayList<>();
    }

    public static NotificationHandler create() {
        return new NotificationHandler();
    }

    @Override
    public ValidationHandler append(String message) {
        this.errors.add(new Error(message));
        return this;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public boolean hasError() {
        return !getErrors().isEmpty();
    }
}
