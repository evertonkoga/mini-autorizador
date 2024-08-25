package br.com.vr.autorizador.application.cartao.get;

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
public class GetCartaoByNumeroUseCaseTest {

    @InjectMocks
    private GetCartaoByNumeroUseCaseImpl useCase;
    @Mock
    private CartaoGateway cartaoGateway;

    @BeforeEach
    void cleanUp() {
        Mockito.reset(cartaoGateway);
    }

    private final String expectedCardNumber = "6549873025634501";
    private final String expectedCardPassword = "1234";
    private final BigDecimal expectedCardBalance = new BigDecimal(500);

    @Test
    public void deveConsultarCartaoPorNumero() {
        final var newCartao = Cartao.newCartao(expectedCardNumber, expectedCardPassword);
        final var cardNumber = newCartao.getNumeroCartao();

        Mockito.when(cartaoGateway.findBy(eq(cardNumber)))
                .thenReturn(Optional.of(newCartao));

        final var cartaoFound = useCase.execute(cardNumber);

        Assertions.assertEquals(expectedCardNumber, cartaoFound.numeroCartao());
        Assertions.assertEquals(expectedCardBalance, cartaoFound.saldo());
    }

    @Test
    public void deveLancarExcecaoAoConsultarCartaoComNumeroInexistente() {
        final String cardNumber = "7549873025634501";
        final String expectedErrorMessage = "Cartão de número %s não encontrado".formatted(cardNumber);

        Mockito.when(cartaoGateway.findBy(eq(cardNumber)))
                .thenReturn(Optional.empty());

        final var actualException = Assertions.assertThrows(
                NotificationException.class, () -> useCase.execute(cardNumber)
        );

        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());
        Mockito.verify(cartaoGateway, times(1)).findBy(any());
    }

    @Test
    public void deveLancarExcecaoAoExcutarGateway() {
        final var expectedErrorMessage = "Gateway error";

        Mockito.when(cartaoGateway.findBy(eq(expectedCardNumber)))
                .thenThrow(new IllegalStateException(expectedErrorMessage));

        final var actualException = Assertions.assertThrows(
                IllegalStateException.class, () -> useCase.execute(expectedCardNumber)
        );

        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());
        Mockito.verify(cartaoGateway, times(1)).findBy(any());
    }
}
