package br.com.vr.autorizador.application;

public interface UseCase<IN, OUT> {
    OUT execute(IN input);
}
