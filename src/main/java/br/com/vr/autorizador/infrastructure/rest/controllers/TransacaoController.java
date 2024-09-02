package br.com.vr.autorizador.infrastructure.rest.controllers;

import br.com.vr.autorizador.application.cartao.debit.DebitCartaoInput;
import br.com.vr.autorizador.application.cartao.debit.DebitCartaoUseCase;
import br.com.vr.autorizador.domain.exceptions.NotificationException;
import br.com.vr.autorizador.infrastructure.cartao.models.DebitCartaoRestInput;
import br.com.vr.autorizador.infrastructure.rest.TransacaoRest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransacaoController implements TransacaoRest {

    private final DebitCartaoUseCase debitCartaoUseCase;

    public TransacaoController(DebitCartaoUseCase debitCartaoUseCase) {
        this.debitCartaoUseCase = debitCartaoUseCase;
    }

    @Override
    public ResponseEntity<?> debit(DebitCartaoRestInput inputRequest) {
        try {
            var input = DebitCartaoInput.with(inputRequest.numeroCartao(), inputRequest.senhaCartao(), inputRequest.valor());
            debitCartaoUseCase.execute(input);
            return ResponseEntity.status(HttpStatus.CREATED).body("OK");
        } catch (NotificationException ne) {
            return ResponseEntity.unprocessableEntity().body(ne.getMessage());
        }
    }
}
