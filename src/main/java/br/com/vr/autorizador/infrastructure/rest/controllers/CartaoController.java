package br.com.vr.autorizador.infrastructure.rest.controllers;

import br.com.vr.autorizador.application.cartao.create.CreateCartaoInput;
import br.com.vr.autorizador.application.cartao.create.CreateCartaoUseCase;
import br.com.vr.autorizador.application.cartao.get.GetCartaoByNumeroUseCase;
import br.com.vr.autorizador.domain.exceptions.NotificationException;
import br.com.vr.autorizador.infrastructure.cartao.models.CreateCartaoRestInput;
import br.com.vr.autorizador.infrastructure.rest.CartaoRest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CartaoController implements CartaoRest {

    private final CreateCartaoUseCase createCartaoUseCase;
    private final GetCartaoByNumeroUseCase getCartaoByNumeroUseCase;

    public CartaoController(CreateCartaoUseCase createCartaoUseCase, GetCartaoByNumeroUseCase getCartaoByNumeroUseCase) {
        this.createCartaoUseCase = createCartaoUseCase;
        this.getCartaoByNumeroUseCase = getCartaoByNumeroUseCase;
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

    @Override
    public ResponseEntity<?> getByNumero(String numeroCartao) {
        try {
            var output = getCartaoByNumeroUseCase.execute(numeroCartao);
            return ResponseEntity.status(HttpStatus.OK).body(output.saldo());
        } catch (NotificationException ne) {
            return ResponseEntity.unprocessableEntity().body(ne);
        }
    }
}
