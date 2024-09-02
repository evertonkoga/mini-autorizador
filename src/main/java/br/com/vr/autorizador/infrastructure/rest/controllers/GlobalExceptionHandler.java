package br.com.vr.autorizador.infrastructure.rest.controllers;

import br.com.vr.autorizador.domain.exceptions.DomainException;
import br.com.vr.autorizador.domain.exceptions.NotFoundException;
import br.com.vr.autorizador.domain.validation.Error;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<?> handleDomainException(NotFoundException dex) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(value = DomainException.class)
    public ResponseEntity<?> handleDomainException(DomainException dex) {
        return ResponseEntity.unprocessableEntity().body(ApiError.from(dex));
    }

    record ApiError(String message, List<Error> errors) {
        static ApiError from(DomainException dex) {
            return new ApiError(dex.getMessage(), dex.getErrors());
        }
    }
}
