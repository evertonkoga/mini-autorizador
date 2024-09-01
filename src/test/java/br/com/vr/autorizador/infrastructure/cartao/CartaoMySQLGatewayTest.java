package br.com.vr.autorizador.infrastructure.cartao;

import br.com.vr.autorizador.domain.cartao.Cartao;
import br.com.vr.autorizador.domain.validation.handler.NotificationHandler;
import br.com.vr.autorizador.infrastructure.MySQLGatewayTest;
import br.com.vr.autorizador.infrastructure.cartao.persistence.CartaoJpaEntity;
import br.com.vr.autorizador.infrastructure.cartao.persistence.CartaoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

@MySQLGatewayTest
public class CartaoMySQLGatewayTest {

    @Autowired
    private CartaoMySQLGateway cartaoGateway;
    @Autowired
    private CartaoRepository cartaoRepository;

    private Cartao newCard;
    private final String expectedCardNumber = "6549873025634501";
    private final String expectedCardPassword = "1234";

    @BeforeEach
    void cleanUp() {
        newCard = Cartao.newCartao("6549873025634501", "1234");
    }

    @Test
    public void deveCriarCartaoComSaldoInicialDe500() {
        final BigDecimal expectedCardBalance = new BigDecimal(500);
        Assertions.assertEquals(0, cartaoRepository.count());

        Cartao cardCreated = cartaoGateway.create(newCard);

        Assertions.assertEquals(1, cartaoRepository.count());
        Assertions.assertEquals(expectedCardNumber, cardCreated.getNumeroCartao());
        Assertions.assertEquals(expectedCardPassword, cardCreated.getSenha());
        Assertions.assertEquals(expectedCardBalance, cardCreated.getSaldo());

        var cardFound = cartaoRepository.findById(cardCreated.getNumeroCartao()).get();
        Assertions.assertEquals(expectedCardNumber, cardFound.getNumeroCartao());
        Assertions.assertEquals(expectedCardPassword, cardFound.getSenha());
        Assertions.assertEquals(expectedCardBalance, cardFound.getSaldo());
    }

    @Test
    public void deveRetornarCartaoAoConsultarComNumeroCartaoValido() {
        final BigDecimal expectedCardBalance = new BigDecimal(500);
        Assertions.assertEquals(0, cartaoRepository.count());

        cartaoRepository.save(CartaoJpaEntity.from(newCard));
        Assertions.assertEquals(1, cartaoRepository.count());

        var cardFound = cartaoGateway.findBy(newCard.getNumeroCartao()).get();
        Assertions.assertEquals(expectedCardNumber, cardFound.getNumeroCartao());
        Assertions.assertEquals(expectedCardPassword, cardFound.getSenha());
        Assertions.assertEquals(expectedCardBalance, cardFound.getSaldo());
    }

    @Test
    public void deveRetornarVazioQuandoInformadoNumeroCartaoInvalido() {
        final String invalidCardNumber = "123";
        Assertions.assertEquals(0, cartaoRepository.count());

        cartaoRepository.save(CartaoJpaEntity.from(newCard));

        final var cardFound = cartaoGateway.findBy(invalidCardNumber);
        Assertions.assertTrue(cardFound.isEmpty());
    }

    @Test
    public void deveDebitarComSucesso() {
        final var expectedBalance = new BigDecimal(490);
        Assertions.assertEquals(0, cartaoRepository.count());

        cartaoRepository.save(CartaoJpaEntity.from(newCard));
        Assertions.assertEquals(1, cartaoRepository.count());

        final var notification = NotificationHandler.create();
        newCard.debit(BigDecimal.TEN, expectedCardPassword, notification);
        cartaoGateway.debit(newCard);
        Assertions.assertEquals(1, cartaoRepository.count());

        var cardFound = cartaoRepository.findById(newCard.getNumeroCartao()).get();
        Assertions.assertEquals(expectedCardNumber, cardFound.getNumeroCartao());
        Assertions.assertEquals(expectedCardPassword, cardFound.getSenha());
        Assertions.assertEquals(expectedBalance, cardFound.getSaldo());
    }

    @Test
    public void deveLancarExcecaoAoDebitarComSenhaInvalida() {
        final String invalidPassword = "4321";
        final BigDecimal debitAmount = BigDecimal.TEN;
        Assertions.assertEquals(0, cartaoRepository.count());

        cartaoRepository.save(CartaoJpaEntity.from(newCard));
        Assertions.assertEquals(1, cartaoRepository.count());

        final var notification = NotificationHandler.create();
        newCard.debit(debitAmount, invalidPassword, notification);
        cartaoGateway.debit(newCard);

        var cardFound = cartaoRepository.findById(newCard.getNumeroCartao()).get();
        Assertions.assertEquals(newCard.getNumeroCartao(), cardFound.getNumeroCartao());
        Assertions.assertEquals(newCard.getSenha(), cardFound.getSenha());
        Assertions.assertEquals(newCard.getSaldo(), cardFound.getSaldo());
    }

    @Test
    public void deveLancarExcecaoAoDebitarComValorMaiorQueSaldo() {
        final BigDecimal debitValueGreaterThanBalance = new BigDecimal(501);
        Assertions.assertEquals(0, cartaoRepository.count());

        cartaoRepository.save(CartaoJpaEntity.from(newCard));
        Assertions.assertEquals(1, cartaoRepository.count());

        final var notification = NotificationHandler.create();
        newCard.debit(debitValueGreaterThanBalance, expectedCardPassword, notification);
        cartaoGateway.debit(newCard);

        var cardFound = cartaoRepository.findById(newCard.getNumeroCartao()).get();
        Assertions.assertEquals(newCard.getNumeroCartao(), cardFound.getNumeroCartao());
        Assertions.assertEquals(newCard.getSenha(), cardFound.getSenha());
        Assertions.assertEquals(newCard.getSaldo(), cardFound.getSaldo());
    }

    @Test
    public void deveLancarExcecaoAoDebitarComValorInvalido() {
        final BigDecimal invalidDebitAmount = null;
        Assertions.assertEquals(0, cartaoRepository.count());

        cartaoRepository.save(CartaoJpaEntity.from(newCard));
        Assertions.assertEquals(1, cartaoRepository.count());

        final var notification = NotificationHandler.create();
        newCard.debit(invalidDebitAmount, expectedCardPassword, notification);
        cartaoGateway.debit(newCard);

        var cardFound = cartaoRepository.findById(newCard.getNumeroCartao()).get();
        Assertions.assertEquals(newCard.getNumeroCartao(), cardFound.getNumeroCartao());
        Assertions.assertEquals(newCard.getSenha(), cardFound.getSenha());
        Assertions.assertEquals(newCard.getSaldo(), cardFound.getSaldo());
    }
}
