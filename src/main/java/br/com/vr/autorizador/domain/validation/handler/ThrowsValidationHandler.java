package br.com.vr.autorizador.domain.validation.handler;

import br.com.vr.autorizador.domain.exceptions.DomainException;
import br.com.vr.autorizador.domain.validation.Error;
import br.com.vr.autorizador.domain.validation.ValidationHandler;

public class ThrowsValidationHandler implements ValidationHandler {
    @Override
    public ValidationHandler append(final String message) {
        throw DomainException.with(new Error(message));
    }
}
