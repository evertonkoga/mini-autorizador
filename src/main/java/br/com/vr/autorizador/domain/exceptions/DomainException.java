package br.com.vr.autorizador.domain.exceptions;

import br.com.vr.autorizador.domain.validation.Error;
import java.util.List;

public class DomainException extends NoStacktraceException {
    private final List<Error> errors;

    private DomainException(final String message, final List<Error> errors) {
        super(message);
        this.errors = errors;
    }

    public static DomainException with(final Error error) {
        return new DomainException(error.message(), List.of(error));
    }

    public List<Error> getErrors() {
        return errors;
    }

    public Error firstError() {
        return getErrors().get(0);
    }
}
