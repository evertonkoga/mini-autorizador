package br.com.vr.autorizador.infrastructure.rest;

import br.com.vr.autorizador.application.cartao.debit.DebitCartaoInput;
import br.com.vr.autorizador.application.cartao.debit.DebitCartaoUseCase;
import br.com.vr.autorizador.domain.exceptions.NotificationException;
import br.com.vr.autorizador.infrastructure.ControllerTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ControllerTest(controllers = TransacaoRest.class)
public class TransacaoRestTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private DebitCartaoUseCase debitCartaoUseCase;

    private final String expectedCardNumber = "6549873025634501";
    private final String expectedCardPassword = "1234";
    private final BigDecimal debitValue = BigDecimal.TEN;

    @Test
    public void deveDebitarComSucesso() throws Exception {
        Mockito.doNothing().when(debitCartaoUseCase).execute(any());

        final var inputRequest = new DebitCartaoInput(expectedCardNumber, expectedCardPassword, debitValue);
        final var request = MockMvcRequestBuilders
                .post("/transacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputRequest));

        mockMvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("OK"));

        Mockito.verify(debitCartaoUseCase, times(1)).execute(any());
    }

    @Test
    public void deveLancarExcecaoAoDebitarComValorInvalido() throws Exception {
        final String expectedErrorMessage = "Valor da transacao invalido";
        final BigDecimal invalidDebitAmount = null;

        Mockito.doThrow(new NotificationException(expectedErrorMessage))
                .when(debitCartaoUseCase).execute(any());

        final var inputRequest = new DebitCartaoInput(expectedCardNumber, expectedCardPassword, invalidDebitAmount);
        final var request = MockMvcRequestBuilders
                .post("/transacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputRequest));

        mockMvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.content().string(expectedErrorMessage));
    }

    @Test
    public void deveLancarExcecaoAoDebitarComValorMaiorQueSaldo() throws Exception {
        final var expectedErrorMessage = "Saldo insuficiente";
        final BigDecimal debitValueGreaterThanBalance = new BigDecimal(501);

        Mockito.doThrow(new NotificationException(expectedErrorMessage))
                .when(debitCartaoUseCase).execute(any());

        final var inputRequest = new DebitCartaoInput(expectedCardNumber, expectedCardPassword, debitValueGreaterThanBalance);
        final var request = MockMvcRequestBuilders
                .post("/transacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputRequest));

        mockMvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.content().string(expectedErrorMessage));
    }

    @Test
    public void deveLancarExcecaoAoDebitarComSenhaInvalida() throws Exception {
        final var expectedErrorMessage = "Senha incorreta";
        final String invalidPassword = "4321";

        Mockito.doThrow(new NotificationException(expectedErrorMessage))
                .when(debitCartaoUseCase).execute(any());

        final var inputRequest = new DebitCartaoInput(expectedCardNumber, invalidPassword, debitValue);
        final var request = MockMvcRequestBuilders
                .post("/transacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputRequest));

        mockMvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.content().string(expectedErrorMessage));
    }

    @Test
    public void deveLancarExcecaoAoInformarNumeroCartaoInvalido() throws Exception {
        final String invalidCardNumber = "7549873025634501";
        final String expectedErrorMessage = "Cartao %s nao encontrado".formatted(invalidCardNumber);

        Mockito.doThrow(new NotificationException(expectedErrorMessage))
                .when(debitCartaoUseCase).execute(any());

        final var inputRequest = new DebitCartaoInput(invalidCardNumber, expectedCardPassword, debitValue);
        final var request = MockMvcRequestBuilders
                .post("/transacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputRequest));

        mockMvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.content().string(expectedErrorMessage));
    }
}
