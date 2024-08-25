package br.com.vr.autorizador.application.cartao.get;

import br.com.vr.autorizador.domain.cartao.Cartao;

import java.math.BigDecimal;

public record GetCartaoByNumeroOutput(String numeroCartao, BigDecimal saldo) {
    public static GetCartaoByNumeroOutput from(Cartao cartao) {
        return new GetCartaoByNumeroOutput(cartao.getNumeroCartao(), cartao.getSaldo());
    }
}
