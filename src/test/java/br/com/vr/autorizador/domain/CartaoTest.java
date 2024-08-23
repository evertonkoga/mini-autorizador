package br.com.vr.autorizador.domain;

import br.com.vr.autorizador.domain.cartao.Cartao;
import br.com.vr.autorizador.domain.exceptions.DomainException;
import br.com.vr.autorizador.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

public class CartaoTest {

    @Test
    public void deveCriarCartaoComSaldoInicialDe500() {
        final String numeroCartaoEsperado = "6549873025634501";
        final BigDecimal saldoCartaoEsperado = new BigDecimal(500);
        final String senhaCartaoEsperado = "1234";

        final var cartaoCriado = Cartao.newCartao(
          numeroCartaoEsperado, senhaCartaoEsperado
        );

        Assertions.assertDoesNotThrow(() -> cartaoCriado.validate(new ThrowsValidationHandler()));
        Assertions.assertNotNull(cartaoCriado);
        Assertions.assertEquals(numeroCartaoEsperado, cartaoCriado.getNumeroCartao());
        Assertions.assertEquals(senhaCartaoEsperado, cartaoCriado.getSenha());
        Assertions.assertEquals(saldoCartaoEsperado, cartaoCriado.getSaldo());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"    "})
    public void deveLancarExcecaoAoCriarCartaoComNumeroNuloOuVazio(String numeroCartao) {
        final var mensagemErroEsperado = "'numeroCartao' é obrigatório";
        final var QuantidadeErroEsperado = 1;

        final String senhaCartaoEsperado = "1234";

        final var cartaoCriado = Cartao.newCartao(
                numeroCartao, senhaCartaoEsperado
        );

        final var actualException = Assertions.assertThrows(
                DomainException.class, () -> cartaoCriado.validate(new ThrowsValidationHandler())
        );

        Assertions.assertEquals(QuantidadeErroEsperado, actualException.getErrors().size());
        Assertions.assertEquals(mensagemErroEsperado, actualException.firstError().message());
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void deveLancarExcecaoAoCriarCartaoComSenhaNulaOuVazia(String senhaCartao) {
        final var mensagemErroEsperado = "'senha' é obrigatória";
        final var QuantidadeErroEsperado = 1;

        final String numeroCartaoEsperado = "6549873025634501";

        final var cartaoCriado = Cartao.newCartao(
                numeroCartaoEsperado, senhaCartao
        );

        final var actualException = Assertions.assertThrows(
                DomainException.class, () -> cartaoCriado.validate(new ThrowsValidationHandler())
        );

        Assertions.assertEquals(QuantidadeErroEsperado, actualException.getErrors().size());
        Assertions.assertEquals(mensagemErroEsperado, actualException.firstError().message());
    }

    @ParameterizedTest
    @ValueSource(strings = {"#549873025634501", "ab498730k25634501"})
    public void deveLancarExcecaoAoCriarCartaoComNumeroInvalido(String numeroCartao) {
        final var mensagemErroEsperado = "'numeroCartao' deve conter apenas numero";
        final var QuantidadeErroEsperado = 1;

        final String senhaCartaoEsperado = "1234";

        final var cartaoCriado = Cartao.newCartao(
                numeroCartao, senhaCartaoEsperado
        );

        final var actualException = Assertions.assertThrows(
                DomainException.class, () -> cartaoCriado.validate(new ThrowsValidationHandler())
        );

        Assertions.assertEquals(QuantidadeErroEsperado, actualException.getErrors().size());
        Assertions.assertEquals(mensagemErroEsperado, actualException.firstError().message());
    }

    @ParameterizedTest
    @ValueSource(strings = {"654987302563450", "65498730256345019"})
    public void deveLancarExcecaoAoCriarCartaoComNumeroDiferenteDe16Digitos(String numeroCartao) {
        final var mensagemErroEsperado = "'numeroCartao' deve possuir 16 caracteres";
        final var QuantidadeErroEsperado = 1;

        final String senhaCartaoEsperado = "1234";

        final var cartaoCriado = Cartao.newCartao(
                numeroCartao, senhaCartaoEsperado
        );

        final var actualException = Assertions.assertThrows(
                DomainException.class, () -> cartaoCriado.validate(new ThrowsValidationHandler())
        );

        Assertions.assertEquals(QuantidadeErroEsperado, actualException.getErrors().size());
        Assertions.assertEquals(mensagemErroEsperado, actualException.firstError().message());
    }
}
