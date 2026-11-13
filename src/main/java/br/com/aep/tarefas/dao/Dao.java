package br.com.aep.tarefas.dao;

import br.com.aep.tarefas.model.EntidadeBase;

import java.util.List;
import java.util.Optional;

/**
 * Interface genérica com as operações de CRUD (Create, Read, Update, Delete)
 * que todo DAO do sistema precisa oferecer.
 */
public interface Dao<T extends EntidadeBase> {

    /** Create: grava a entidade e preenche o id gerado pelo banco. */
    void inserir(T entidade);

    /** Read: busca uma entidade pela chave primária. */
    Optional<T> buscarPorId(int id);

    /** Read: lista todas as entidades da tabela. */
    List<T> listarTodos();

    /** Update: grava as alterações de uma entidade já existente. */
    void atualizar(T entidade);

    /** Delete: remove a entidade; devolve false se o id não existia. */
    boolean excluir(int id);
}
