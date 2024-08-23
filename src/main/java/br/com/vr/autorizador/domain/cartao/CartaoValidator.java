package br.com.vr.autorizador.domain.cartao;

import br.com.vr.autorizador.domain.validation.ValidationHandler;
import br.com.vr.autorizador.domain.validation.Validator;
import org.apache.commons.lang3.StringUtils;

public class CartaoValidator extends Validator {

    public static final int CARD_NUMBER_SIZE = 16;
    private final Cartao cartao;

    public CartaoValidator(final Cartao cartao, final ValidationHandler handler) {
        super(handler);
        this.cartao = cartao;
    }
    @Override
    public void validate() {
        if (StringUtils.isBlank(this.cartao.getSenha())) {
            this.validationHandler().append("'senha' é obrigatória");
        }

        if (StringUtils.isBlank(this.cartao.getNumeroCartao())) {
            this.validationHandler().append("'numeroCartao' é obrigatório");
        }

        if(!StringUtils.isNumeric(this.cartao.getNumeroCartao())) {
            this.validationHandler().append("'numeroCartao' deve conter apenas numero");
        }

        final var length = this.cartao.getNumeroCartao().trim().length();
        if(length != CARD_NUMBER_SIZE) {
            this.validationHandler().append("'numeroCartao' deve possuir 16 caracteres");
        }
    }
}
