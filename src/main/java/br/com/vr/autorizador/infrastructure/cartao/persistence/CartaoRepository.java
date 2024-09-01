package br.com.vr.autorizador.infrastructure.cartao.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartaoRepository extends JpaRepository<CartaoJpaEntity, String> {
}
