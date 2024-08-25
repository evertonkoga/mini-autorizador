package br.com.vr.autorizador.application.cartao.debit;

import br.com.vr.autorizador.domain.cartao.Cartao;
import br.com.vr.autorizador.domain.cartao.CartaoGateway;
import br.com.vr.autorizador.domain.exceptions.NotificationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class DebitCartaoUseCaseTest {

    @InjectMocks
    private DebitCartaoUseCaseImpl useCase;
    @Mock
    private CartaoGateway cartaoGateway;

    private Cartao newCartao;
    private final String expectedCardNumber = "6549873025634501";
    private final String expectedCardPassword = "1234";
    private final BigDecimal debitValue = BigDecimal.TEN;
    private final Integer expectedErrorNumber = 1;

    @BeforeEach
    void cleanUp() {
        Mockito.reset(cartaoGateway);
        newCartao = Cartao.newCartao(expectedCardNumber, expectedCardPassword);
    }

    @Test
    public void deveDebitarComSucesso() {
        final var cardNumber = newCartao.getNumeroCartao();
        Mockito.when(cartaoGateway.findBy(eq(cardNumber)))
                .thenReturn(Optional.of(newCartao));

        final var input = DebitCartaoInput.with(cardNumber, expectedCardPassword, debitValue);
        useCase.execute(input);

        Mockito.verify(cartaoGateway, times(1)).findBy(eq(cardNumber));
        Mockito.verify(cartaoGateway, times(1)).debit(eq(newCartao));
    }

    @Test
    public void deveLancarExcecaoAoDebitarComValorInvalido() {
        final String expectedErrorMessage = "Valor da transação inválido";
        final BigDecimal invalidDebitAmount = null;

        final var cardNumber = newCartao.getNumeroCartao();
        Mockito.when(cartaoGateway.findBy(eq(cardNumber)))
                .thenReturn(Optional.of(newCartao));

        final var input = DebitCartaoInput.with(cardNumber, expectedCardPassword, invalidDebitAmount);
        final var actualException = Assertions.assertThrows(
                NotificationException.class, () -> useCase.execute(input)
        );

        Assertions.assertEquals(expectedErrorNumber, actualException.numberOfErrors());
        Assertions.assertEquals(expectedErrorMessage, actualException.firstError().message());

        Mockito.verify(cartaoGateway, times(1)).findBy(eq(cardNumber));
        Mockito.verify(cartaoGateway, times(0)).debit(any());
    }

    @Test
    public void deveLancarExcecaoAoDebitarComValorMaiorQueSaldo() {
        final var expectedErrorMessage = "Saldo insuficiente";
        final BigDecimal debitValueGreaterThanBalance = new BigDecimal(501);

        final var cardNumber = newCartao.getNumeroCartao();
        Mockito.when(cartaoGateway.findBy(eq(cardNumber)))
                .thenReturn(Optional.of(newCartao));

        final var input = DebitCartaoInput.with(cardNumber, expectedCardPassword, debitValueGreaterThanBalance);
        final var actualException = Assertions.assertThrows(
                NotificationException.class, () -> useCase.execute(input)
        );

        Assertions.assertEquals(expectedErrorNumber, actualException.numberOfErrors());
        Assertions.assertEquals(expectedErrorMessage, actualException.firstError().message());

        Mockito.verify(cartaoGateway, times(1)).findBy(eq(cardNumber));
        Mockito.verify(cartaoGateway, times(0)).debit(any());
    }

    @Test
    public void deveLancarExcecaoAoDebitarComSenhaInvalida() {
        final var expectedErrorMessage = "Senha incorreta";
        final String invalidPassword = "4321";

        final var cardNumber = newCartao.getNumeroCartao();
        Mockito.when(cartaoGateway.findBy(eq(cardNumber)))
                .thenReturn(Optional.of(newCartao));

        final var input = DebitCartaoInput.with(cardNumber, invalidPassword, debitValue);
        final var actualException = Assertions.assertThrows(
                NotificationException.class, () -> useCase.execute(input)
        );

        Assertions.assertEquals(expectedErrorNumber, actualException.numberOfErrors());
        Assertions.assertEquals(expectedErrorMessage, actualException.firstError().message());

        Mockito.verify(cartaoGateway, times(1)).findBy(eq(cardNumber));
        Mockito.verify(cartaoGateway, times(0)).debit(any());
    }

    @Test
    public void deveLancarExcecaoAoInformarNumeroCartaoInvalido() {
        final String invalidCardNumber = "7549873025634501";
        final String expectedErrorMessage = "Cartão de número %s não encontrado".formatted(invalidCardNumber);

        Mockito.when(cartaoGateway.findBy(eq(invalidCardNumber)))
                .thenReturn(Optional.empty());

        final var input = DebitCartaoInput.with(invalidCardNumber, expectedCardPassword, debitValue);
        final var actualException = Assertions.assertThrows(
                NotificationException.class, () -> useCase.execute(input)
        );

        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());

        Mockito.verify(cartaoGateway, times(1)).findBy(eq(invalidCardNumber));
        Mockito.verify(cartaoGateway, times(0)).debit(any());
    }

    @Test
    public void deveLancarExcecaoAoExcutarGateway() {
        final var expectedErrorMessage = "Gateway error";

        final var cardNumber = newCartao.getNumeroCartao();
        Mockito.when(cartaoGateway.findBy(eq(cardNumber)))
                .thenReturn(Optional.of(newCartao));

        Mockito.doThrow(new IllegalStateException(expectedErrorMessage))
                .when(cartaoGateway)
                .debit(any());

        final var input = DebitCartaoInput.with(expectedCardNumber, expectedCardPassword, debitValue);
        final var actualException = Assertions.assertThrows(
                IllegalStateException.class, () -> useCase.execute(input)
        );

        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());
        Mockito.verify(cartaoGateway, times(1)).findBy(eq(cardNumber));
        Mockito.verify(cartaoGateway, times(1)).debit(eq(newCartao));
    }
}
