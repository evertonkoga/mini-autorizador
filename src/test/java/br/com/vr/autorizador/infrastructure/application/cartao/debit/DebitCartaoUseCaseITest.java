package br.com.vr.autorizador.infrastructure.application.cartao.debit;

import br.com.vr.autorizador.application.cartao.debit.DebitCartaoInput;
import br.com.vr.autorizador.application.cartao.debit.DebitCartaoUseCase;
import br.com.vr.autorizador.domain.cartao.Cartao;
import br.com.vr.autorizador.domain.cartao.CartaoGateway;
import br.com.vr.autorizador.domain.exceptions.NotificationException;
import br.com.vr.autorizador.infrastructure.IntegrationTest;
import br.com.vr.autorizador.infrastructure.cartao.persistence.CartaoJpaEntity;
import br.com.vr.autorizador.infrastructure.cartao.persistence.CartaoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@IntegrationTest
public class DebitCartaoUseCaseITest {

    @Autowired
    private DebitCartaoUseCase useCase;
    @Autowired
    private CartaoRepository cartaoRepository;
    @SpyBean
    private CartaoGateway cartaoGateway;

    private Cartao newCartao;
    private final String expectedCardNumber = "6549873025634501";
    private final String expectedCardPassword = "1234";
    private final BigDecimal expectedCardBalance = new BigDecimal("500.00");
    private final BigDecimal debitValue = BigDecimal.TEN;
    private final Integer expectedErrorNumber = 1;

    @BeforeEach
    void cleanUp() {
        newCartao = Cartao.newCartao(expectedCardNumber, expectedCardPassword);
        Assertions.assertEquals(0, cartaoRepository.count());
        cartaoRepository.saveAndFlush(CartaoJpaEntity.from(newCartao));
        Assertions.assertEquals(1, cartaoRepository.count());
    }

    @Test
    public void deveDebitarComSucesso() {
        final BigDecimal expectedCardBalance = new BigDecimal("490.00");
        final var cardNumber = newCartao.getNumeroCartao();

        final var input = DebitCartaoInput.with(cardNumber, expectedCardPassword, debitValue);
        useCase.execute(input);

        var cardFound = cartaoRepository.findById(cardNumber).get();
        Assertions.assertEquals(expectedCardNumber, cardFound.getNumeroCartao());
        Assertions.assertEquals(expectedCardPassword, cardFound.getSenha());
        Assertions.assertEquals(expectedCardBalance, cardFound.getSaldo());
    }

    @Test
    public void deveLancarExcecaoAoDebitarComValorInvalido() {
        final String expectedErrorMessage = "Valor da transação inválido";
        final BigDecimal invalidDebitAmount = null;

        final var cardNumber = newCartao.getNumeroCartao();

        final var input = DebitCartaoInput.with(cardNumber, expectedCardPassword, invalidDebitAmount);
        final var actualException = Assertions.assertThrows(
                NotificationException.class, () -> useCase.execute(input)
        );

        Assertions.assertEquals(expectedErrorNumber, actualException.numberOfErrors());
        Assertions.assertEquals(expectedErrorMessage, actualException.firstError().message());

        var cardFound = cartaoRepository.findById(cardNumber).get();
        Assertions.assertEquals(expectedCardNumber, cardFound.getNumeroCartao());
        Assertions.assertEquals(expectedCardPassword, cardFound.getSenha());
        Assertions.assertEquals(expectedCardBalance, cardFound.getSaldo());
        Mockito.verify(cartaoGateway, times(0)).debit(any());
    }

    @Test
    public void deveLancarExcecaoAoDebitarComValorMaiorQueSaldo() {
        final var expectedErrorMessage = "Saldo insuficiente";
        final BigDecimal debitValueGreaterThanBalance = new BigDecimal(501);

        final var cardNumber = newCartao.getNumeroCartao();

        final var input = DebitCartaoInput.with(cardNumber, expectedCardPassword, debitValueGreaterThanBalance);
        final var actualException = Assertions.assertThrows(
                NotificationException.class, () -> useCase.execute(input)
        );

        Assertions.assertEquals(expectedErrorNumber, actualException.numberOfErrors());
        Assertions.assertEquals(expectedErrorMessage, actualException.firstError().message());

        var cardFound = cartaoRepository.findById(cardNumber).get();
        Assertions.assertEquals(expectedCardNumber, cardFound.getNumeroCartao());
        Assertions.assertEquals(expectedCardPassword, cardFound.getSenha());
        Assertions.assertEquals(expectedCardBalance, cardFound.getSaldo());
        Mockito.verify(cartaoGateway, times(0)).debit(any());
    }

    @Test
    public void deveLancarExcecaoAoDebitarComSenhaInvalida() {
        final var expectedErrorMessage = "Senha incorreta";
        final String invalidPassword = "4321";

        final var cardNumber = newCartao.getNumeroCartao();

        final var input = DebitCartaoInput.with(cardNumber, invalidPassword, debitValue);
        final var actualException = Assertions.assertThrows(
                NotificationException.class, () -> useCase.execute(input)
        );

        Assertions.assertEquals(expectedErrorNumber, actualException.numberOfErrors());
        Assertions.assertEquals(expectedErrorMessage, actualException.firstError().message());

        var cardFound = cartaoRepository.findById(cardNumber).get();
        Assertions.assertEquals(expectedCardNumber, cardFound.getNumeroCartao());
        Assertions.assertEquals(expectedCardPassword, cardFound.getSenha());
        Assertions.assertEquals(expectedCardBalance, cardFound.getSaldo());
        Mockito.verify(cartaoGateway, times(0)).debit(any());
    }

    @Test
    public void deveLancarExcecaoAoInformarNumeroCartaoInvalido() {
        final String invalidCardNumber = "7549873025634501";
        final String expectedErrorMessage = "Cartao %s nao encontrado".formatted(invalidCardNumber);

        final var input = DebitCartaoInput.with(invalidCardNumber, expectedCardPassword, debitValue);
        final var actualException = Assertions.assertThrows(
                NotificationException.class, () -> useCase.execute(input)
        );

        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());

        var cardFound = cartaoRepository.findById(newCartao.getNumeroCartao()).get();
        Assertions.assertEquals(expectedCardNumber, cardFound.getNumeroCartao());
        Assertions.assertEquals(expectedCardPassword, cardFound.getSenha());
        Assertions.assertEquals(expectedCardBalance, cardFound.getSaldo());
        Mockito.verify(cartaoGateway, times(0)).debit(any());
    }

    @Test
    public void deveLancarExcecaoAoExcutarGateway() {
        final var expectedErrorMessage = "Gateway error";

        final var cardNumber = newCartao.getNumeroCartao();

        Mockito.doThrow(new IllegalStateException(expectedErrorMessage))
                .when(cartaoGateway)
                .debit(any());

        final var input = DebitCartaoInput.with(expectedCardNumber, expectedCardPassword, debitValue);
        final var actualException = Assertions.assertThrows(
                IllegalStateException.class, () -> useCase.execute(input)
        );

        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());

        var cardFound = cartaoRepository.findById(cardNumber).get();
        Assertions.assertEquals(expectedCardNumber, cardFound.getNumeroCartao());
        Assertions.assertEquals(expectedCardPassword, cardFound.getSenha());
        Assertions.assertEquals(expectedCardBalance, cardFound.getSaldo());
    }
}
