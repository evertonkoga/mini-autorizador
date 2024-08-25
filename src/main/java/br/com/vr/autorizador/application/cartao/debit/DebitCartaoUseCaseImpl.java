package br.com.vr.autorizador.application.cartao.debit;

import br.com.vr.autorizador.domain.cartao.CartaoGateway;
import br.com.vr.autorizador.domain.exceptions.NotificationException;
import br.com.vr.autorizador.domain.validation.handler.NotificationHandler;

import java.util.Objects;

public class DebitCartaoUseCaseImpl implements DebitCartaoUseCase {

    private final CartaoGateway cartaoGateway;

    public DebitCartaoUseCaseImpl(CartaoGateway cartaoGateway) {
        this.cartaoGateway = Objects.requireNonNull(cartaoGateway);
    }

    @Override
    public void execute(DebitCartaoInput input) {
        final String numeroCartao = input.numeroCartao();
        final var cartao = cartaoGateway.findBy(numeroCartao)
                .orElseThrow(() -> new NotificationException(
                                "Cartão de número %s não encontrado".formatted(numeroCartao)
                        )
                );

        final var notification = NotificationHandler.create();
        cartao.debit(input.valor(), input.senhaCartao(), notification);

        if (notification.hasError()) {
            throw NotificationException.with("Não foi possível criar o cartão", notification);
        }

        this.cartaoGateway.debit(cartao);
    }
}
