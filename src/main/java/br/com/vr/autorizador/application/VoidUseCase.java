package br.com.vr.autorizador.application;

public interface VoidUseCase<IN> {
    void execute(IN input);
}
