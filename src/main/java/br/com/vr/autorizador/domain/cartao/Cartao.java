package br.com.vr.autorizador.domain.cartao;

import br.com.vr.autorizador.domain.validation.ValidationHandler;

import java.math.BigDecimal;

public class Cartao {

    private static final BigDecimal NEW_CARD_INITIAL_BALANCE = new BigDecimal(500);

    private Long id;
    private String numeroCartao;
    private String senha;
    private BigDecimal saldo;

    private Cartao(String numeroCartao, String senha, BigDecimal saldo) {
        this.numeroCartao = numeroCartao;
        this.senha = senha;
        this.saldo = saldo;
    }

    public static Cartao newCartao(String numeroCartao, String senha) {
        return new Cartao(numeroCartao, senha, NEW_CARD_INITIAL_BALANCE);
    }

    public void validate(ValidationHandler handler) {
        new CartaoValidator(this, handler).validate();
    }

    public Long getId() {
        return id;
    }

    public String getNumeroCartao() {
        return numeroCartao;
    }

    public String getSenha() {
        return senha;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }
}
