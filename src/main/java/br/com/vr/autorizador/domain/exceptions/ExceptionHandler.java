package br.com.vr.autorizador.domain.exceptions;

import br.com.vr.autorizador.domain.validation.Error;

public interface ExceptionHandler {
    int numberOfErrors();
    Error firstError();
}
