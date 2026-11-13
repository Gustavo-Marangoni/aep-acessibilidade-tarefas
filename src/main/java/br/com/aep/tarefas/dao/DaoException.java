package br.com.aep.tarefas.dao;

/** Erro de acesso ao banco de dados, com mensagem amigável para o usuário. */
public class DaoException extends RuntimeException {

    public DaoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
