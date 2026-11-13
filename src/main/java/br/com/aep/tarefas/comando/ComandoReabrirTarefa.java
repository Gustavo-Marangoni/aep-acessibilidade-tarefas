package br.com.aep.tarefas.comando;

import br.com.aep.tarefas.model.Tarefa;

/** RF06 - volta uma tarefa concluída para "Pendente". */
public class ComandoReabrirTarefa extends ComandoTarefaSelecionada {

    public ComandoReabrirTarefa() {
        super(5, "Reabrir tarefa (voltar para pendente)", "reabrir tarefa", "marcar como pendente");
    }

    @Override
    protected void aplicar(ContextoAplicacao contexto, Tarefa tarefa) {
        Tarefa reaberta = contexto.getTarefaService().reabrir(contexto.getUsuario(), tarefa.getId());
        contexto.getSaida().mensagem("Tarefa reaberta. " + reaberta.resumo());
    }
}
