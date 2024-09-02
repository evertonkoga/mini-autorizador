package br.com.vr.autorizador.infrastructure.cartao.models;

import java.math.BigDecimal;

public record DebitCartaoRestInput(
        String numeroCartao,
        String senhaCartao,
        BigDecimal valor
) {
}
