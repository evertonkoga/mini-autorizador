package br.com.vr.autorizador.application.cartao.create;

import br.com.vr.autorizador.domain.cartao.Cartao;
import br.com.vr.autorizador.domain.cartao.CartaoGateway;
import br.com.vr.autorizador.domain.exceptions.NotificationException;
import br.com.vr.autorizador.domain.validation.handler.NotificationHandler;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CreateCartaoUseCaseImpl implements CreateCartaoUseCase {
    private final CartaoGateway cartaoGateway;

    public CreateCartaoUseCaseImpl(CartaoGateway cartaoGateway) {
        this.cartaoGateway = Objects.requireNonNull(cartaoGateway);
    }

    @Override
    public CreateCartaoOutput execute(CreateCartaoInput input) {
        final var notification = NotificationHandler.create();
        final var cartao = Cartao.newCartao(input.numeroCartao(), input.senha());
        cartao.validate(notification);

        if (notification.hasError()) {
            throw NotificationException.with("Não foi possível criar o cartão", notification);
        }

        return CreateCartaoOutput.from(this.cartaoGateway.create(cartao));
    }
}
