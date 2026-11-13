package br.com.aep.tarefas.model;

/** Lançada quando uma regra de negócio (RN01..RN07) não é respeitada. */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
