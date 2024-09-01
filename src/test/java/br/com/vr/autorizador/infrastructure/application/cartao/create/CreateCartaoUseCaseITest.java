package br.com.vr.autorizador.infrastructure.application.cartao.create;

import br.com.vr.autorizador.application.cartao.create.CreateCartaoInput;
import br.com.vr.autorizador.application.cartao.create.CreateCartaoUseCase;
import br.com.vr.autorizador.domain.cartao.CartaoGateway;
import br.com.vr.autorizador.domain.exceptions.NotificationException;
import br.com.vr.autorizador.infrastructure.IntegrationTest;
import br.com.vr.autorizador.infrastructure.cartao.persistence.CartaoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@IntegrationTest
public class CreateCartaoUseCaseITest {

    private static final String STRING_WITH_SPACE_ONLY = "    ";

    @Autowired
    private CreateCartaoUseCase useCase;
    @Autowired
    private CartaoRepository cartaoRepository;
    @SpyBean
    private CartaoGateway cartaoGateway;

    private final String expectedCardNumber = "6549873025634501";
    private final String expectedCardPassword = "1234";
    private final Integer expectedErrorNumber = 1;

    @Test
    public void deveCriarCartaoComSaldoInicialDe500() {
        final BigDecimal expectedCardBalance = new BigDecimal("500.00");

        Assertions.assertEquals(0, cartaoRepository.count());

        final var input = CreateCartaoInput.with(expectedCardNumber, expectedCardPassword);
        final var cardCreated = useCase.execute(input);

        Assertions.assertNotNull(cardCreated);
        Assertions.assertNotNull(cardCreated.numeroCartao());
        Assertions.assertEquals(1, cartaoRepository.count());

        var cardFound = cartaoRepository.findById(cardCreated.numeroCartao()).get();
        Assertions.assertEquals(expectedCardNumber, cardFound.getNumeroCartao());
        Assertions.assertEquals(expectedCardPassword, cardFound.getSenha());
        Assertions.assertEquals(expectedCardBalance, cardFound.getSaldo());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {STRING_WITH_SPACE_ONLY})
    public void deveLancarExcecaoAoCriarCartaoComNumeroNuloOuVazio(String numeroCartao) {
        final var expectedErrorMessage = "'numeroCartao' é obrigatório";

        Assertions.assertEquals(0, cartaoRepository.count());

        final var input = CreateCartaoInput.with(numeroCartao, expectedCardPassword);
        final var actualException = Assertions.assertThrows(
                NotificationException.class, () -> useCase.execute(input)
        );

        Assertions.assertEquals(expectedErrorNumber, actualException.numberOfErrors());
        Assertions.assertEquals(expectedErrorMessage, actualException.firstError().message());
        Assertions.assertEquals(0, cartaoRepository.count());
        Mockito.verify(cartaoGateway, times(0)).create(any());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {STRING_WITH_SPACE_ONLY})
    public void deveLancarExcecaoAoCriarCartaoComSenhaNulaOuVazia(String senha) {
        final var expectedErrorMessage = "'senha' é obrigatória";

        Assertions.assertEquals(0, cartaoRepository.count());

        final var input = CreateCartaoInput.with(expectedCardNumber, senha);
        final var actualException = Assertions.assertThrows(
                NotificationException.class, () -> useCase.execute(input)
        );

        Assertions.assertEquals(0, cartaoRepository.count());
        Assertions.assertEquals(expectedErrorNumber, actualException.numberOfErrors());
        Assertions.assertEquals(expectedErrorMessage, actualException.firstError().message());
        Mockito.verify(cartaoGateway, times(0)).create(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"#549873025634501", "ab498730k2563450"})
    public void deveLancarExcecaoAoCriarCartaoComNumeroInvalido(String numeroCartao) {
        final var expectedErrorMessage = "'numeroCartao' deve conter apenas numero";

        Assertions.assertEquals(0, cartaoRepository.count());

        final var input = CreateCartaoInput.with(numeroCartao, expectedCardPassword);
        final var actualException = Assertions.assertThrows(
                NotificationException.class, () -> useCase.execute(input)
        );

        Assertions.assertEquals(expectedErrorNumber, actualException.numberOfErrors());
        Assertions.assertEquals(expectedErrorMessage, actualException.firstError().message());
        Assertions.assertEquals(0, cartaoRepository.count());
        Mockito.verify(cartaoGateway, times(0)).create(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"654987302563450", "65498730256345019"})
    public void deveLancarExcecaoAoCriarCartaoComNumeroDiferenteDe16Digitos(String numeroCartao) {
        final var expectedErrorMessage = "'numeroCartao' deve possuir 16 caracteres";

        Assertions.assertEquals(0, cartaoRepository.count());

        final var input = CreateCartaoInput.with(numeroCartao, expectedCardPassword);
        final var actualException = Assertions.assertThrows(
                NotificationException.class, () -> useCase.execute(input)
        );

        Assertions.assertEquals(expectedErrorNumber, actualException.numberOfErrors());
        Assertions.assertEquals(expectedErrorMessage, actualException.firstError().message());
        Assertions.assertEquals(0, cartaoRepository.count());
        Mockito.verify(cartaoGateway, times(0)).create(any());
    }

    @Test
    public void deveLancarExcecaoAoExcutarGateway() {
        final var expectedErrorMessage = "Gateway error";

        Mockito.doThrow(new NotificationException(expectedErrorMessage))
                .when(cartaoGateway)
                .create(any());

        final var input = CreateCartaoInput.with(expectedCardNumber, expectedCardPassword);
        final var actualException = Assertions.assertThrows(
                NotificationException.class, () -> useCase.execute(input)
        );
        Assertions.assertEquals(expectedErrorNumber, actualException.numberOfErrors());
        Assertions.assertEquals(expectedErrorMessage, actualException.firstError().message());
    }
}
