package br.com.vr.autorizador.infrastructure.cartao.models;

public record CreateCartaoRestInput(
        String numeroCartao,
        String senha
) {
}
