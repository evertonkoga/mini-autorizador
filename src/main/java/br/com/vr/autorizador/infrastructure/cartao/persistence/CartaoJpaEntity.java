package br.com.vr.autorizador.infrastructure.cartao.persistence;

import br.com.vr.autorizador.domain.cartao.Cartao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "cartao")
public class CartaoJpaEntity {
    @Id
    @Column(name = "numero_cartao", nullable = false)
    private String numeroCartao;
    @Column(nullable = false)
    private String senha;
    @Column(nullable = false)
    private BigDecimal saldo;

    public CartaoJpaEntity(){}

    private CartaoJpaEntity(String numeroCartao, String senha, BigDecimal saldo) {
        this.numeroCartao = numeroCartao;
        this.senha = senha;
        this.saldo = saldo;
    }

    public static CartaoJpaEntity from(Cartao cartao){
        return new CartaoJpaEntity(cartao.getNumeroCartao(), cartao.getSenha(), cartao.getSaldo());
    }

    public Cartao toCartao(){
        return Cartao.with(getNumeroCartao(), getSenha(), getSaldo());
    }

    public String getNumeroCartao() {
        return numeroCartao;
    }

    public void setNumeroCartao(String numeroCartao) {
        this.numeroCartao = numeroCartao;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
}
