package br.com.aep.tarefas.comando;

import java.util.List;
import java.util.function.Supplier;

/** Lê/mostra todos os comandos disponíveis. */
public class ComandoAjuda extends Comando {

    private final Supplier<List<Comando>> comandos;

    public ComandoAjuda(Supplier<List<Comando>> comandos) {
        super(9, "Ajuda (ouvir os comandos)", "ajuda", "comandos", "menu", "o que posso falar");
        this.comandos = comandos;
    }

    @Override
    public void executar(ContextoAplicacao contexto) {
        contexto.getSaida().lista("Comandos disponíveis",
                comandos.get().stream().map(Comando::linhaDoMenu).toList());
        contexto.getSaida().mensagem("Durante uma pergunta, diga cancelar para voltar ao menu.");
    }
}
