package br.com.vr.autorizador.application.cartao.debit;

import java.math.BigDecimal;

public record DebitCartaoInput(String numeroCartao, String senhaCartao, BigDecimal valor) {
    public static DebitCartaoInput with(String numeroCartao, String senhaCartao, BigDecimal valor){
        return new DebitCartaoInput(numeroCartao, senhaCartao, valor);
    }
}
