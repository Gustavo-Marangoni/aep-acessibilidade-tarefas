package br.com.aep.tarefas.service;

import br.com.aep.tarefas.dao.TarefaDao;
import br.com.aep.tarefas.model.RegraNegocioException;
import br.com.aep.tarefas.model.Tarefa;
import br.com.aep.tarefas.model.Usuario;

import java.time.LocalDate;
import java.util.List;

/** Regras de negócio das tarefas (RF02 a RF06). Toda operação grava no MySQL. */
public class TarefaService {

    private final TarefaDao tarefaDao;

    public TarefaService(TarefaDao tarefaDao) {
        this.tarefaDao = tarefaDao;
    }

    /** RF02 - cadastra uma tarefa (sempre começa como Pendente). */
    public Tarefa cadastrar(Usuario usuario, String titulo, String descricao, LocalDate data) {
        Tarefa tarefa = new Tarefa(limpar(titulo), vazioParaNulo(descricao), data);
        usuario.adicionarTarefa(tarefa);
        try {
            tarefa.validar();
            tarefaDao.inserir(tarefa);
        } catch (RuntimeException e) {
            usuario.removerTarefa(tarefa.getId());
            throw e;
        }
        return tarefa;
    }

    /** RF03 - consulta as tarefas do usuário direto do banco. */
    public List<Tarefa> listar(Usuario usuario) {
        List<Tarefa> tarefas = tarefaDao.listarPorUsuario(usuario.getId());
        usuario.substituirTarefas(tarefas);
        return usuario.listarTarefas();
    }

    /** RF03 - consulta uma tarefa específica, garantindo que ela é do usuário. */
    public Tarefa buscar(Usuario usuario, int idTarefa) {
        return tarefaDao.buscarPorId(idTarefa)
                .filter(t -> t.getIdUsuario() == usuario.getId())
                .orElseThrow(() -> new RegraNegocioException("Você não tem nenhuma tarefa com o número " + idTarefa + "."));
    }

    /** RF04 - altera título, descrição e data. */
    public Tarefa alterar(Usuario usuario, int idTarefa, String titulo, String descricao, LocalDate data) {
        Tarefa tarefa = buscar(usuario, idTarefa);
        tarefa.alterar(limpar(titulo), vazioParaNulo(descricao), data);
        tarefaDao.atualizar(tarefa);
        return tarefa;
    }

    /** RF05 - exclui a tarefa. */
    public Tarefa excluir(Usuario usuario, int idTarefa) {
        Tarefa tarefa = buscar(usuario, idTarefa);
        tarefaDao.excluir(tarefa.getId());
        usuario.removerTarefa(tarefa.getId());
        return tarefa;
    }

    /** RF06 - marca como Concluída. */
    public Tarefa concluir(Usuario usuario, int idTarefa) {
        Tarefa tarefa = buscar(usuario, idTarefa);
        tarefa.concluir();
        tarefaDao.atualizar(tarefa);
        return tarefa;
    }

    /** RF06 - volta para Pendente. */
    public Tarefa reabrir(Usuario usuario, int idTarefa) {
        Tarefa tarefa = buscar(usuario, idTarefa);
        tarefa.reabrir();
        tarefaDao.atualizar(tarefa);
        return tarefa;
    }

    private static String limpar(String texto) {
        return texto == null ? null : texto.trim();
    }

    private static String vazioParaNulo(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }
}
