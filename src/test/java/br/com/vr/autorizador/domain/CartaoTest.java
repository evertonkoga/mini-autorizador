package br.com.vr.autorizador.domain;

import br.com.vr.autorizador.domain.cartao.Cartao;
import br.com.vr.autorizador.domain.exceptions.DomainException;
import br.com.vr.autorizador.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

public class CartaoTest {
    private static final String STRING_WITH_SPACE_ONLY = "    ";
    private Cartao cardCreated;
    private final String expectedCardNumber = "6549873025634501";
    private final String expectedCardPassword = "1234";
    private final Integer expectedErrorNumber = 1;

    @BeforeEach
    void setUp() {
        cardCreated = Cartao.newCartao("6549873025634501", "1234");
    }

    @Test
    public void deveCriarCartaoComSaldoInicialDe500() {
        final BigDecimal expectedCardBalance = new BigDecimal(500);

        Assertions.assertDoesNotThrow(() -> cardCreated.validate(new ThrowsValidationHandler()));
        Assertions.assertNotNull(cardCreated);
        Assertions.assertEquals(expectedCardNumber, cardCreated.getNumeroCartao());
        Assertions.assertEquals(expectedCardPassword, cardCreated.getSenha());
        Assertions.assertEquals(expectedCardBalance, cardCreated.getSaldo());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {STRING_WITH_SPACE_ONLY})
    public void deveLancarExcecaoAoCriarCartaoComNumeroNuloOuVazio(String numeroCartao) {
        final var expectedErrorMessage = "'numeroCartao' é obrigatório";

        final var cardCreated = Cartao.newCartao(numeroCartao, expectedCardPassword);

        final var actualException = Assertions.assertThrows(
                DomainException.class, () -> cardCreated.validate(new ThrowsValidationHandler())
        );

        Assertions.assertEquals(expectedErrorNumber, actualException.numberOfErrors());
        Assertions.assertEquals(expectedErrorMessage, actualException.firstError().message());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {STRING_WITH_SPACE_ONLY})
    public void deveLancarExcecaoAoCriarCartaoComSenhaNulaOuVazia(String senha) {
        final var expectedErrorMessage = "'senha' é obrigatória";

        final var cardCreated = Cartao.newCartao(expectedCardNumber, senha);

        final var actualException = Assertions.assertThrows(
                DomainException.class, () -> cardCreated.validate(new ThrowsValidationHandler())
        );

        Assertions.assertEquals(expectedErrorNumber, actualException.numberOfErrors());
        Assertions.assertEquals(expectedErrorMessage, actualException.firstError().message());
    }

    @ParameterizedTest
    @ValueSource(strings = {"#549873025634501", "ab498730k25634501"})
    public void deveLancarExcecaoAoCriarCartaoComNumeroInvalido(String numeroCartao) {
        final var expectedErrorMessage = "'numeroCartao' deve conter apenas numero";

        final var cardCreated = Cartao.newCartao(numeroCartao, expectedCardPassword);

        final var actualException = Assertions.assertThrows(
                DomainException.class, () -> cardCreated.validate(new ThrowsValidationHandler())
        );

        Assertions.assertEquals(expectedErrorNumber, actualException.numberOfErrors());
        Assertions.assertEquals(expectedErrorMessage, actualException.firstError().message());
    }

    @ParameterizedTest
    @ValueSource(strings = {"654987302563450", "65498730256345019"})
    public void deveLancarExcecaoAoCriarCartaoComNumeroDiferenteDe16Digitos(String numeroCartao) {
        final var expectedErrorMessage = "'numeroCartao' deve possuir 16 caracteres";

        final var cardCreated = Cartao.newCartao(numeroCartao, expectedCardPassword);

        final var actualException = Assertions.assertThrows(
                DomainException.class, () -> cardCreated.validate(new ThrowsValidationHandler())
        );

        Assertions.assertEquals(expectedErrorNumber, actualException.numberOfErrors());
        Assertions.assertEquals(expectedErrorMessage, actualException.firstError().message());
    }

    private static Stream<BigDecimal> invalidDebitAmountProvider() {
        return Stream.of(null, BigDecimal.ZERO, new BigDecimal("-10"));
    }

    @ParameterizedTest
    @MethodSource("invalidDebitAmountProvider")
    public void deveLancarExcecaoAoDebitarComValorInvalido(BigDecimal valorDebito) {
        final var expectedErrorMessage = "Valor da transação inválido";

        Assertions.assertDoesNotThrow(() -> cardCreated.validate(new ThrowsValidationHandler()));
        Assertions.assertNotNull(cardCreated);

        final var actualException = Assertions.assertThrows(
            DomainException.class, () -> cardCreated.debitFromBalance(
                    valorDebito, expectedCardPassword, new ThrowsValidationHandler()
                )
        );
        Assertions.assertEquals(expectedErrorNumber, actualException.numberOfErrors());
        Assertions.assertEquals(expectedErrorMessage, actualException.firstError().message());
    }

    @Test
    public void deveLancarExcecaoAoDebitarComValorMaiorQueSaldo() {
        final var expectedErrorMessage = "Saldo insuficiente";
        final BigDecimal debitValueGreaterThanBalance = new BigDecimal(501);

        Assertions.assertDoesNotThrow(() -> cardCreated.validate(new ThrowsValidationHandler()));
        Assertions.assertNotNull(cardCreated);

        final var actualException = Assertions.assertThrows(
                DomainException.class, () -> cardCreated.debitFromBalance(
                        debitValueGreaterThanBalance, expectedCardPassword, new ThrowsValidationHandler()
                )
        );
        Assertions.assertEquals(expectedErrorNumber, actualException.numberOfErrors());
        Assertions.assertEquals(expectedErrorMessage, actualException.firstError().message());
    }

    private static Stream<Object[]> debitAmountProvider() {
        return Stream.of(
                new Object[]{
                        List.of(
                                new BigDecimal("100"), new BigDecimal("100"), new BigDecimal("100"),
                                new BigDecimal("100"), new BigDecimal("100")
                        ),
                        BigDecimal.ZERO
                },
                new Object[]{
                        List.of(
                                new BigDecimal("5"), new BigDecimal("15"),
                                new BigDecimal("25"), new BigDecimal("5")
                        ),
                        new BigDecimal("450")
                },
                new Object[]{
                        List.of(new BigDecimal("10"), new BigDecimal("5.99"), new BigDecimal("10")),
                        new BigDecimal("474.01")
                }
        );
    }

    @ParameterizedTest
    @MethodSource("debitAmountProvider")
    public void deveDebitarComSucesso(List<BigDecimal> listaDebitos, BigDecimal saldoFinal) {
        Assertions.assertDoesNotThrow(() -> cardCreated.validate(new ThrowsValidationHandler()));
        Assertions.assertNotNull(cardCreated);

        listaDebitos.forEach(debitAmount -> {
            cardCreated.debitFromBalance(debitAmount, expectedCardPassword, new ThrowsValidationHandler());
        });

        Assertions.assertEquals(saldoFinal, cardCreated.getSaldo());
    }

    @Test
    public void deveLancarExcecaoAoDebitarComSenhaInvalida() {
        final var expectedErrorMessage = "Senha incorreta";
        final BigDecimal debitValue = BigDecimal.TEN;
        final String invalidPassword = "4321";

        Assertions.assertDoesNotThrow(() -> cardCreated.validate(new ThrowsValidationHandler()));
        Assertions.assertNotNull(cardCreated);

        final var actualException = Assertions.assertThrows(
                DomainException.class, () -> cardCreated.debitFromBalance(
                        debitValue, invalidPassword, new ThrowsValidationHandler()
                )
        );
        Assertions.assertEquals(expectedErrorNumber, actualException.numberOfErrors());
        Assertions.assertEquals(expectedErrorMessage, actualException.firstError().message());
    }
}
