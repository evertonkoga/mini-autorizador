package br.com.vr.autorizador.infrastructure.application.cartao.get;

import br.com.vr.autorizador.application.cartao.get.GetCartaoByNumeroUseCase;
import br.com.vr.autorizador.domain.cartao.Cartao;
import br.com.vr.autorizador.domain.cartao.CartaoGateway;
import br.com.vr.autorizador.domain.exceptions.NotificationException;
import br.com.vr.autorizador.infrastructure.IntegrationTest;
import br.com.vr.autorizador.infrastructure.cartao.persistence.CartaoJpaEntity;
import br.com.vr.autorizador.infrastructure.cartao.persistence.CartaoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;

@IntegrationTest
public class GetCartaoByNumeroUseCaseITest {

    @Autowired
    private GetCartaoByNumeroUseCase useCase;
    @Autowired
    private CartaoRepository cartaoRepository;
    @SpyBean
    private CartaoGateway cartaoGateway;
    private final String expectedCardNumber = "6549873025634501";
    private final String expectedCardPassword = "1234";
    private final BigDecimal expectedCardBalance = new BigDecimal("500.00");

    @Test
    public void deveConsultarCartaoPorNumero() {
        Assertions.assertEquals(0, cartaoRepository.count());

        final var newCartao = Cartao.newCartao(expectedCardNumber, expectedCardPassword);
        final var cardNumber = newCartao.getNumeroCartao();

        cartaoRepository.saveAndFlush(CartaoJpaEntity.from(newCartao));

        final var cartaoFound = useCase.execute(cardNumber);

        Assertions.assertEquals(expectedCardNumber, cartaoFound.numeroCartao());
        Assertions.assertEquals(expectedCardBalance, cartaoFound.saldo());
    }

    @Test
    public void deveLancarExcecaoAoConsultarCartaoComNumeroInexistente() {
        final String cardNumber = "7549873025634501";
        final String expectedErrorMessage = "Cartão de número %s não encontrado".formatted(cardNumber);

        final var actualException = Assertions.assertThrows(
                NotificationException.class, () -> useCase.execute(cardNumber)
        );

        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());
    }

    @Test
    public void deveLancarExcecaoAoExcutarGateway() {
        final var expectedErrorMessage = "Gateway error";

        doThrow(new IllegalStateException(expectedErrorMessage))
                .when(cartaoGateway)
                .findBy(eq(expectedCardNumber));

        final var actualException = Assertions.assertThrows(
                IllegalStateException.class, () -> useCase.execute(expectedCardNumber)
        );

        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());
    }
}
