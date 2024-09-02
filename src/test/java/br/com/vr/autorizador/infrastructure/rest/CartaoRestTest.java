package br.com.vr.autorizador.infrastructure.rest;

import br.com.vr.autorizador.application.cartao.create.CreateCartaoOutput;
import br.com.vr.autorizador.application.cartao.create.CreateCartaoUseCase;
import br.com.vr.autorizador.domain.cartao.Cartao;
import br.com.vr.autorizador.domain.exceptions.DomainException;
import br.com.vr.autorizador.domain.exceptions.NotificationException;
import br.com.vr.autorizador.domain.validation.Error;
import br.com.vr.autorizador.infrastructure.ControllerTest;
import br.com.vr.autorizador.infrastructure.cartao.models.CreateCartaoRestInput;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;

@ControllerTest(controllers = CartaoRest.class)
public class CartaoRestTest {

    private static final String STRING_WITH_SPACE_ONLY = "    ";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    CreateCartaoUseCase createCartaoUseCase;

    private Cartao newCartao;
    private final String expectedCardNumber = "6549873025634501";
    private final String expectedCardPassword = "1234";

    @BeforeEach
    void cleanUp() {
        newCartao = Cartao.newCartao(expectedCardNumber, expectedCardPassword);
    }

    @Test
    public void deveCriarCartaoComSaldoInicialDe500() throws Exception {
        Mockito.when(createCartaoUseCase.execute(any()))
                .thenReturn(CreateCartaoOutput.from(newCartao));

        final var inputRequest = new CreateCartaoRestInput(expectedCardNumber, expectedCardPassword);
        final var request = MockMvcRequestBuilders
                .post("/cartoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputRequest));

        mockMvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.numeroCartao", Matchers.equalTo(expectedCardNumber)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.senha", Matchers.equalTo(expectedCardPassword)));

        Mockito.verify(createCartaoUseCase, times(1)).execute(argThat(cartao ->
                Objects.equals(expectedCardNumber, cartao.numeroCartao())
                        && Objects.equals(expectedCardPassword, cartao.senha())
        ));
    }

    @Test
    public void deveLancarExcecaoAoCriarCartaoComNumeroJaCadastrado() throws Exception {
        final var expectedErrorMessage = "Cartão já existente";

        Mockito.when(createCartaoUseCase.execute(any()))
                .thenThrow(new NotificationException(expectedErrorMessage));

        final var inputRequest = new CreateCartaoRestInput(expectedCardNumber, expectedCardPassword);
        final var request = MockMvcRequestBuilders
                .post("/cartoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputRequest));

        mockMvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.numeroCartao", Matchers.equalTo(expectedCardNumber)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.senha", Matchers.equalTo(expectedCardPassword)));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {STRING_WITH_SPACE_ONLY})
    public void deveLancarExcecaoAoCriarCartaoComNumeroNuloOuVazio(String numeroCartao) throws Exception {
        final var expectedErrorMessage = "'numeroCartao' é obrigatório";

        Mockito.when(createCartaoUseCase.execute(any()))
                .thenThrow(new NotificationException(expectedErrorMessage));

        final var inputRequest = new CreateCartaoRestInput(numeroCartao, expectedCardPassword);
        final var request = MockMvcRequestBuilders
                .post("/cartoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputRequest));

        mockMvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.numeroCartao", Matchers.equalTo(numeroCartao)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.senha", Matchers.equalTo(expectedCardPassword)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"#549873025634501", "ab498730k2563450"})
    public void deveLancarExcecaoAoCriarCartaoComNumeroInvalido(String numeroCartao) throws Exception {
        final var expectedErrorMessage = "'numeroCartao' deve conter apenas numero";

        Mockito.when(createCartaoUseCase.execute(any()))
                .thenThrow(new NotificationException(expectedErrorMessage));

        final var inputRequest = new CreateCartaoRestInput(numeroCartao, expectedCardPassword);
        final var request = MockMvcRequestBuilders
                .post("/cartoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputRequest));

        mockMvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.numeroCartao", Matchers.equalTo(numeroCartao)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.senha", Matchers.equalTo(expectedCardPassword)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"654987302563450", "65498730256345019"})
    public void deveLancarExcecaoAoCriarCartaoComNumeroDiferenteDe16Digitos(String numeroCartao) throws Exception {
        final var expectedErrorMessage = "'numeroCartao' deve possuir 16 caracteres";

        Mockito.when(createCartaoUseCase.execute(any()))
                .thenThrow(new NotificationException(expectedErrorMessage));

        final var inputRequest = new CreateCartaoRestInput(numeroCartao, expectedCardPassword);
        final var request = MockMvcRequestBuilders
                .post("/cartoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputRequest));

        mockMvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.numeroCartao", Matchers.equalTo(numeroCartao)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.senha", Matchers.equalTo(expectedCardPassword)));
    }

    @Test
    public void deveTratarELancarExcecaoAoCriarCartaoComErroNaoTratados() throws Exception {
        final var expectedErrorMessage = "'numeroCartao' é obrigatório";

        Mockito.when(createCartaoUseCase.execute(any()))
                .thenThrow(DomainException.with(new Error(expectedErrorMessage)));

        final var inputRequest = new CreateCartaoRestInput(expectedCardNumber, expectedCardPassword);
        final var request = MockMvcRequestBuilders
                .post("/cartoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputRequest));

        mockMvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.equalTo(expectedErrorMessage)));
    }
}
