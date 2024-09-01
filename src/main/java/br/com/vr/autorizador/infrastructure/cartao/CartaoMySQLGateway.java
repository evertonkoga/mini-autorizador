package br.com.vr.autorizador.infrastructure.cartao;

import br.com.vr.autorizador.domain.cartao.Cartao;
import br.com.vr.autorizador.domain.cartao.CartaoGateway;
import br.com.vr.autorizador.infrastructure.cartao.persistence.CartaoJpaEntity;
import br.com.vr.autorizador.infrastructure.cartao.persistence.CartaoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartaoMySQLGateway implements CartaoGateway {

    private final CartaoRepository repository;

    public CartaoMySQLGateway(CartaoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Cartao create(Cartao cartao) {
        return save(cartao);
    }

    @Override
    public Optional<Cartao> findBy(String numeroCartao) {
        return repository.findById(numeroCartao).map(CartaoJpaEntity::toCartao);
    }

    @Override
    public void debit(Cartao cartao) {
        save(cartao);
    }

    private Cartao save(Cartao cartao) {
        return repository.save(CartaoJpaEntity.from(cartao)).toCartao();
    }
}
