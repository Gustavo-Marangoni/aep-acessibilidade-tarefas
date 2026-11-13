package br.com.aep.tarefas.comando;

import br.com.aep.tarefas.interacao.TipoResposta;
import br.com.aep.tarefas.model.Tarefa;

import java.time.LocalDate;

/** RF02 - cadastro de tarefa com título, descrição, data e status. */
public class ComandoCadastrarTarefa extends Comando {

    public ComandoCadastrarTarefa() {
        super(1, "Nova tarefa", "nova tarefa", "cadastrar tarefa", "criar tarefa", "adicionar tarefa");
    }

    @Override
    public void executar(ContextoAplicacao contexto) {
        String titulo = contexto.perguntar("Qual o título da tarefa?", TipoResposta.TEXTO_LIVRE);
        String descricao = contexto.perguntar("Descrição da tarefa (deixe vazio para pular):",
                TipoResposta.TEXTO_LIVRE);
        if (descricao.equalsIgnoreCase("pular")) {
            descricao = "";
        }
        LocalDate data = contexto.perguntarData(
                "Para quando? Diga hoje, amanhã, sem data, ou digite dd/mm/aaaa.");
        Tarefa tarefa = contexto.getTarefaService().cadastrar(contexto.getUsuario(), titulo, descricao, data);
        contexto.getSaida().mensagem("Tarefa cadastrada! " + tarefa.resumo());
    }
}
