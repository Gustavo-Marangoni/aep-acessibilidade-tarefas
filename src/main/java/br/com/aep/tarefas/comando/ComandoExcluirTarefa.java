package br.com.aep.tarefas.comando;

import br.com.aep.tarefas.model.Tarefa;

/** RF05 - exclusão de tarefa, com confirmação. */
public class ComandoExcluirTarefa extends ComandoTarefaSelecionada {

    public ComandoExcluirTarefa() {
        super(6, "Excluir tarefa", "excluir tarefa", "apagar tarefa", "remover tarefa");
    }

    @Override
    protected void aplicar(ContextoAplicacao contexto, Tarefa tarefa) {
        if (!contexto.perguntarSimNao("Excluir \"" + tarefa.getTitulo() + "\" definitivamente?")) {
            contexto.getSaida().mensagem("Nada foi excluído.");
            return;
        }
        contexto.getTarefaService().excluir(contexto.getUsuario(), tarefa.getId());
        contexto.getSaida().mensagem("Tarefa \"" + tarefa.getTitulo() + "\" excluída.");
    }
}
