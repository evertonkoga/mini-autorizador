package br.com.vr.autorizador.domain.exceptions;

import br.com.vr.autorizador.domain.validation.Error;

import java.util.Collections;
import java.util.List;

public class NotFoundException extends DomainException {
    protected NotFoundException(String message, List<Error> errors) {
        super(message, errors);
    }

    public static NotFoundException with(
            Class<?> classe, String numeroCartao
    ) {
        String message = "%s %s não encontrado".formatted(classe.getSimpleName(), numeroCartao);
        return new NotFoundException(message, Collections.emptyList());
    }
}
