package br.com.vr.autorizador.application.cartao.create;

public record CreateCartaoInput(String numeroCartao, String senha) {
    public static CreateCartaoInput with(String numeroCartao, String senha){
        return new CreateCartaoInput(numeroCartao, senha);
    }
}
