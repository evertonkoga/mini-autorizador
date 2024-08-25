package br.com.vr.autorizador.domain.cartao;

import java.util.Optional;

public interface CartaoGateway {
    Cartao create(Cartao cartao);
    Optional<Cartao> findBy(String numeroCartao);
}
