package br.com.vr.autorizador.application.cartao.get;

import br.com.vr.autorizador.domain.cartao.Cartao;
import br.com.vr.autorizador.domain.cartao.CartaoGateway;
import br.com.vr.autorizador.domain.exceptions.NotFoundException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class GetCartaoByNumeroUseCaseImpl implements GetCartaoByNumeroUseCase {

    private final CartaoGateway cartaoGateway;

    public GetCartaoByNumeroUseCaseImpl(CartaoGateway cartaoGateway) {
        this.cartaoGateway = Objects.requireNonNull(cartaoGateway);
    }

    @Override
    public GetCartaoByNumeroOutput execute(String numeroCartao) {
        return cartaoGateway.findBy(numeroCartao)
                .map(GetCartaoByNumeroOutput::from)
                .orElseThrow(() -> NotFoundException.with(Cartao.class, numeroCartao));
    }
}
