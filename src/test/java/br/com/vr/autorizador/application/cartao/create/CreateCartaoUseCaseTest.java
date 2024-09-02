package br.com.vr.autorizador.application.cartao.create;

import br.com.vr.autorizador.domain.cartao.Cartao;
import br.com.vr.autorizador.domain.cartao.CartaoGateway;
import br.com.vr.autorizador.domain.exceptions.NotificationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateCartaoUseCaseTest {
    private static final String STRING_WITH_SPACE_ONLY = "    ";
    @InjectMocks
    private CreateCartaoUseCaseImpl useCase;
    @Mock
    private CartaoGateway cartaoGateway;

    private final String expectedCardNumber = "6549873025634501";
    private final String expectedCardPassword = "1234";
    private final BigDecimal expectedCardBalance = new BigDecimal(500);
    private final Integer expectedErrorNumber = 1;

    @BeforeEach
    void cleanUp() {
        Mockito.reset(cartaoGateway);
    }

    @Test
    public void deveCriarCartaoComSaldoInicialDe500() {
        Mockito.when(cartaoGateway.create(any())).thenAnswer(returnsFirstArg());

        final var input = CreateCartaoInput.with(expectedCardNumber, expectedCardPassword);
        final var cardCreated = useCase.execute(input);

        Assertions.assertNotNull(cardCreated);
        Assertions.assertNotNull(cardCreated.numeroCartao());

        Mockito.verify(cartaoGateway, times(1)).create(argThat(cartao ->
                Objects.equals(expectedCardNumber, cartao.getNumeroCartao())
                        && Objects.equals(expectedCardPassword, cartao.getSenha())
                        && Objects.equals(expectedCardBalance, cartao.getSaldo())
        ));
    }

    @Test
    public void deveLancarExcecaoAoCriarCartaoComNumeroJaCadastrado() {
        final var expectedErrorMessage = "Cartão já existente";

        Cartao newCartao = Cartao.newCartao(expectedCardNumber, expectedCardPassword);
        Mockito.when(cartaoGateway.findBy(any()))
                .thenReturn(Optional.of(newCartao));

        final var input = CreateCartaoInput.with(expectedCardNumber, expectedCardPassword);
        final var actualException = Assertions.assertThrows(
                NotificationException.class, () -> useCase.execute(input)
        );

        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());

        Mockito.verify(cartaoGateway, times(0)).create(any());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {STRING_WITH_SPACE_ONLY})
    public void deveLancarExcecaoAoCriarCartaoComNumeroNuloOuVazio(String numeroCartao) {
        final var expectedErrorMessage = "'numeroCartao' é obrigatório";

        final var input = CreateCartaoInput.with(numeroCartao, expectedCardPassword);
        final var actualException = Assertions.assertThrows(
                NotificationException.class, () -> useCase.execute(input)
        );

        Assertions.assertEquals(expectedErrorNumber, actualException.numberOfErrors());
        Assertions.assertEquals(expectedErrorMessage, actualException.firstError().message());

        Mockito.verify(cartaoGateway, times(0)).create(any());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {STRING_WITH_SPACE_ONLY})
    public void deveLancarExcecaoAoCriarCartaoComSenhaNulaOuVazia(String senha) {
        final var expectedErrorMessage = "'senha' é obrigatória";

        final var input = CreateCartaoInput.with(expectedCardNumber, senha);
        final var actualException = Assertions.assertThrows(
                NotificationException.class, () -> useCase.execute(input)
        );

        Assertions.assertEquals(expectedErrorNumber, actualException.numberOfErrors());
        Assertions.assertEquals(expectedErrorMessage, actualException.firstError().message());

        Mockito.verify(cartaoGateway, times(0)).create(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"#549873025634501", "ab498730k2563450"})
    public void deveLancarExcecaoAoCriarCartaoComNumeroInvalido(String numeroCartao) {
        final var expectedErrorMessage = "'numeroCartao' deve conter apenas numero";

        final var input = CreateCartaoInput.with(numeroCartao, expectedCardPassword);
        final var actualException = Assertions.assertThrows(
                NotificationException.class, () -> useCase.execute(input)
        );

        Assertions.assertEquals(expectedErrorNumber, actualException.numberOfErrors());
        Assertions.assertEquals(expectedErrorMessage, actualException.firstError().message());

        Mockito.verify(cartaoGateway, times(0)).create(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"654987302563450", "65498730256345019"})
    public void deveLancarExcecaoAoCriarCartaoComNumeroDiferenteDe16Digitos(String numeroCartao) {
        final var expectedErrorMessage = "'numeroCartao' deve possuir 16 caracteres";

        final var input = CreateCartaoInput.with(numeroCartao, expectedCardPassword);
        final var actualException = Assertions.assertThrows(
                NotificationException.class, () -> useCase.execute(input)
        );

        Assertions.assertEquals(expectedErrorNumber, actualException.numberOfErrors());
        Assertions.assertEquals(expectedErrorMessage, actualException.firstError().message());

        Mockito.verify(cartaoGateway, times(0)).create(any());
    }

    @Test
    public void deveLancarExcecaoAoExcutarGateway() {
        final var expectedErrorMessage = "Gateway error";

        when(cartaoGateway.create(any()))
                .thenThrow(new NotificationException(expectedErrorMessage));

        final var input = CreateCartaoInput.with(expectedCardNumber, expectedCardPassword);
        final var actualException = Assertions.assertThrows(
                NotificationException.class, () -> useCase.execute(input)
        );
        Assertions.assertEquals(expectedErrorNumber, actualException.numberOfErrors());
        Assertions.assertEquals(expectedErrorMessage, actualException.firstError().message());

        Mockito.verify(cartaoGateway, times(1)).create(argThat(cartao ->
                Objects.equals(expectedCardNumber, cartao.getNumeroCartao())
                        && Objects.equals(expectedCardPassword, cartao.getSenha())
                        && Objects.equals(expectedCardBalance, cartao.getSaldo())
        ));
    }
}
