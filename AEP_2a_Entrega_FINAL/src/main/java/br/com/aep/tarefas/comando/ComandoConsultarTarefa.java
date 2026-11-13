package br.com.aep.tarefas.comando;

import br.com.aep.tarefas.model.Tarefa;

import java.util.List;

/** RF03 - consulta das tarefas cadastradas (lidas do banco). */
public class ComandoConsultarTarefa extends Comando {

    public ComandoConsultarTarefa() {
        super(2, "Listar tarefas", "listar tarefas", "consultar tarefas", "ver tarefas", "minhas tarefas");
    }

    @Override
    public void executar(ContextoAplicacao contexto) {
        List<Tarefa> tarefas = contexto.getTarefaService().listar(contexto.getUsuario());
        long pendentes = tarefas.stream().filter(t -> !t.isConcluida()).count();
        contexto.getSaida().lista("Você tem " + tarefas.size() + " tarefas, " + pendentes + " pendentes",
                tarefas.stream().map(Tarefa::detalhes).toList());
    }
}
