package br.com.aep.tarefas.comando;

/** Encerra a sessão do usuário e volta para a tela inicial. */
public class ComandoSair extends Comando {

    public ComandoSair() {
        super(0, "Sair da conta", "sair", "encerrar", "fechar", "tchau");
    }

    @Override
    public void executar(ContextoAplicacao contexto) {
        contexto.getSaida().mensagem("Até logo, " + contexto.getUsuario().getNome() + "!");
        contexto.encerrarSessao();
    }
}
