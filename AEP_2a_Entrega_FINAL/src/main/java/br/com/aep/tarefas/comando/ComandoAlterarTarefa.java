package br.com.aep.tarefas.comando;

import br.com.aep.tarefas.interacao.TipoResposta;
import br.com.aep.tarefas.model.Tarefa;
import br.com.aep.tarefas.util.Texto;

import java.time.LocalDate;

/** RF04 - alteração de título, descrição e data de uma tarefa. */
public class ComandoAlterarTarefa extends ComandoTarefaSelecionada {

    public ComandoAlterarTarefa() {
        super(3, "Alterar tarefa", "alterar tarefa", "editar tarefa", "mudar tarefa");
    }

    @Override
    protected void aplicar(ContextoAplicacao contexto, Tarefa tarefa) {
        contexto.getSaida().mensagem("Alterando: " + tarefa.detalhes()
                + " Para manter um campo, deixe vazio ou diga manter.");

        String titulo = contexto.perguntar("Novo título:", TipoResposta.TEXTO_LIVRE);
        String descricao = contexto.perguntar("Nova descrição (diga apagar para remover):", TipoResposta.TEXTO_LIVRE);
        String textoData = contexto.perguntar("Nova data (hoje, amanhã, sem data ou dd/mm/aaaa):", TipoResposta.DATA);

        String novoTitulo = Texto.ehManter(titulo) ? tarefa.getTitulo() : titulo;
        String novaDescricao;
        if (Texto.normalizar(descricao).equals("apagar")) {
            novaDescricao = null;
        } else {
            novaDescricao = Texto.ehManter(descricao) ? tarefa.getDescricao() : descricao;
        }
        LocalDate novaData = Texto.ehManter(textoData) ? tarefa.getData() : Texto.paraData(textoData, LocalDate.now());

        Tarefa alterada = contexto.getTarefaService().alterar(contexto.getUsuario(), tarefa.getId(),
                novoTitulo, novaDescricao, novaData);
        contexto.getSaida().mensagem("Tarefa alterada! " + alterada.resumo());
    }
}
