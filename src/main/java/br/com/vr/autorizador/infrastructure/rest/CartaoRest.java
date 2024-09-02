package br.com.vr.autorizador.infrastructure.rest;

import br.com.vr.autorizador.infrastructure.cartao.models.CreateCartaoRestInput;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping(value = "cartoes")
public interface CartaoRest {

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<?> createCartao(@RequestBody CreateCartaoRestInput inputRequest);
}
