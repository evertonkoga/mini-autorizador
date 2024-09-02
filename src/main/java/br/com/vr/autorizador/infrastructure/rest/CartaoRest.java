package br.com.vr.autorizador.infrastructure.rest;

import br.com.vr.autorizador.infrastructure.cartao.models.CreateCartaoRestInput;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "cartoes")
public interface CartaoRest {

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<?> createCartao(@RequestBody CreateCartaoRestInput inputRequest);

    @GetMapping(path = "/{numeroCartao}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<?> getByNumero(@PathVariable("numeroCartao") String numeroCartao);
}
