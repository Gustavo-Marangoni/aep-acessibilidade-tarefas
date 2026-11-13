package br.com.aep.tarefas.comando;

/** Lançada quando o usuário diz/digita "cancelar" no meio de um comando. */
public class OperacaoCanceladaException extends RuntimeException {

    public OperacaoCanceladaException() {
        super("Operação cancelada.");
    }
}
