package br.com.vr.autorizador.domain.cartao;

import br.com.vr.autorizador.domain.validation.ValidationHandler;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Objects;

public class Cartao {

    private static final BigDecimal NEW_CARD_INITIAL_BALANCE = new BigDecimal(500);

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

    public static Cartao with(String numeroCartao, String senha, BigDecimal saldo) {
        return new Cartao(numeroCartao, senha, saldo);
    }

    public void validate(ValidationHandler handler) {
        new CartaoValidator(this, handler).validate();
    }

    public void debit(BigDecimal debitValue, String password, ValidationHandler handler) {
        if (!validateSenha(password)) {
            handler.append("Senha incorreta");
            return;
        }

        if (debitValue == null || debitValue.compareTo(BigDecimal.ZERO) <= 0) {
            handler.append("Valor da transação inválido");
            return;
        }

        if (saldo.compareTo(debitValue) < 0) {
            handler.append("Saldo insuficiente");
            return;
        }

        updateBalance(saldo.subtract(debitValue));
    }

    private void updateBalance(BigDecimal newBalance) {
        this.saldo = newBalance;
    }

    private boolean validateSenha(String password) {
        return StringUtils.isNotBlank(password) && this.senha.equals(password);
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
