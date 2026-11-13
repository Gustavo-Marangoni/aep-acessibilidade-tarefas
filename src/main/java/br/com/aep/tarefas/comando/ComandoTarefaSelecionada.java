package br.com.aep.tarefas.comando;

import br.com.aep.tarefas.model.Tarefa;

import java.util.List;

/**
 * Comando intermediário (também abstrato) para as ações que começam pedindo
 * o número de uma tarefa: alterar, excluir, concluir e reabrir. Usa o padrão
 * Template Method: executar() é fixo e chama aplicar(), que cada subclasse
 * sobrescreve.
 */
public abstract class ComandoTarefaSelecionada extends Comando {

    protected ComandoTarefaSelecionada(int numero, String descricao, String... frases) {
        super(numero, descricao, frases);
    }

    @Override
    public final void executar(ContextoAplicacao contexto) {
        List<Tarefa> tarefas = contexto.getTarefaService().listar(contexto.getUsuario());
        if (tarefas.isEmpty()) {
            contexto.getSaida().mensagem("Você ainda não tem tarefas cadastradas.");
            return;
        }
        contexto.getSaida().lista("Suas tarefas", tarefas.stream().map(Tarefa::resumo).toList());
        int idTarefa = contexto.perguntarNumero("Qual o número da tarefa? Diga cancelar para voltar.");
        Tarefa tarefa = contexto.getTarefaService().buscar(contexto.getUsuario(), idTarefa);
        aplicar(contexto, tarefa);
    }

    /** Ação específica sobre a tarefa escolhida. */
    protected abstract void aplicar(ContextoAplicacao contexto, Tarefa tarefa);
}
