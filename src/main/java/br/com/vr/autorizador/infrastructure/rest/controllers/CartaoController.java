package br.com.vr.autorizador.infrastructure.rest.controllers;

import br.com.vr.autorizador.application.cartao.create.CreateCartaoInput;
import br.com.vr.autorizador.application.cartao.create.CreateCartaoUseCase;
import br.com.vr.autorizador.domain.exceptions.NotificationException;
import br.com.vr.autorizador.infrastructure.cartao.models.CreateCartaoRestInput;
import br.com.vr.autorizador.infrastructure.rest.CartaoRest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CartaoController implements CartaoRest {

    private final CreateCartaoUseCase createCartaoUseCase;

    public CartaoController(CreateCartaoUseCase createCartaoUseCase) {
        this.createCartaoUseCase = createCartaoUseCase;
    }

    @Override
    public ResponseEntity<?> createCartao(CreateCartaoRestInput inputRequest) {
        try {
            var input = CreateCartaoInput.with(inputRequest.numeroCartao(), inputRequest.senha());
            var output = createCartaoUseCase.execute(input);
            return ResponseEntity.status(HttpStatus.CREATED).body(output);
        } catch (NotificationException ne) {
            return ResponseEntity.unprocessableEntity().body(inputRequest);
        }
    }
}
