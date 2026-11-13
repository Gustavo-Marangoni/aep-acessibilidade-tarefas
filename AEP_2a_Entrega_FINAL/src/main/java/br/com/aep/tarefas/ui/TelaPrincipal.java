package br.com.aep.tarefas.ui;

import br.com.aep.tarefas.comando.Comando;
import br.com.aep.tarefas.comando.ContextoAplicacao;
import br.com.aep.tarefas.comando.GerenciadorComandos;
import br.com.aep.tarefas.comando.OperacaoCanceladaException;
import br.com.aep.tarefas.dao.DaoException;
import br.com.aep.tarefas.interacao.TipoResposta;
import br.com.aep.tarefas.model.RegraNegocioException;

import java.util.Optional;

/** Menu principal depois do login: interpreta frases e executa comandos. */
public class TelaPrincipal {

    private final ContextoAplicacao contexto;
    private final GerenciadorComandos gerenciador;

    public TelaPrincipal(ContextoAplicacao contexto, GerenciadorComandos gerenciador) {
        this.contexto = contexto;
        this.gerenciador = gerenciador;
    }

    public void exibir() {
        contexto.setFrasesDeComando(gerenciador::frasesDeVoz);
        mostrarMenu();
        while (contexto.isSessaoAtiva()) {
            try {
                String frase = contexto.perguntar("Qual comando?", TipoResposta.COMANDO);
                Optional<Comando> comando = gerenciador.interpretar(frase);
                if (comando.isPresent()) {
                    comando.get().executar(contexto); // polimorfismo: cada subclasse faz o seu
                } else if (!frase.isBlank()) {
                    contexto.getSaida().erro("Comando \"" + frase + "\" não reconhecido. Diga ajuda para ouvir as opções.");
                }
            } catch (OperacaoCanceladaException e) {
                if (contexto.isAplicacaoAtiva()) {
                    contexto.getSaida().mensagem(e.getMessage());
                }
            } catch (RegraNegocioException | DaoException e) {
                contexto.getSaida().erro(e.getMessage());
            }
        }
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("============== MENU ==============");
        gerenciador.getComandos().forEach(c -> System.out.println(c.linhaDoMenu()));
        System.out.println("==================================");
        System.out.println("Digite o número ou a frase" + (contexto.getEntrada().isVoz() ? ", ou fale o comando." : "."));
        System.out.println("Em qualquer pergunta, \"cancelar\" volta ao menu.");
        if (contexto.getEntrada().isVoz()) {
            contexto.getSaida().mensagem("Comandos de voz ligados. Diga ajuda para ouvir as opções.");
        }
    }
}
