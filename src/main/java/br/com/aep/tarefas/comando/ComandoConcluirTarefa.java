package br.com.aep.tarefas.comando;

import br.com.aep.tarefas.model.Tarefa;

/** RF06 - marca a tarefa como "Concluída". */
public class ComandoConcluirTarefa extends ComandoTarefaSelecionada {

    public ComandoConcluirTarefa() {
        super(4, "Concluir tarefa", "concluir tarefa", "finalizar tarefa", "marcar como concluida");
    }

    @Override
    protected void aplicar(ContextoAplicacao contexto, Tarefa tarefa) {
        Tarefa concluida = contexto.getTarefaService().concluir(contexto.getUsuario(), tarefa.getId());
        contexto.getSaida().mensagem("Parabéns! " + concluida.resumo());
    }
}
