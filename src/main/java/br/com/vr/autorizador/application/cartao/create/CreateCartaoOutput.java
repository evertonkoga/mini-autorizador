package br.com.vr.autorizador.application.cartao.create;

import br.com.vr.autorizador.domain.cartao.Cartao;

public record CreateCartaoOutput(String numeroCartao, String senha) {
    public static CreateCartaoOutput from(Cartao cartao) {
        return new CreateCartaoOutput(cartao.getNumeroCartao(), cartao.getSenha());
    }
}
