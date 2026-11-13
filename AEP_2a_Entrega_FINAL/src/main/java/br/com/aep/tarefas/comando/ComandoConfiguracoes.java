package br.com.aep.tarefas.comando;

import br.com.aep.tarefas.model.ConfiguracaoAcessibilidade;
import br.com.aep.tarefas.model.Usuario;

import java.util.ArrayList;
import java.util.List;

/** RF07 - consulta e altera as configurações de acessibilidade do usuário. */
public class ComandoConfiguracoes extends Comando {

    public ComandoConfiguracoes() {
        super(7, "Configurações de acessibilidade", "configuracoes", "acessibilidade", "ajustes");
    }

    @Override
    public void executar(ContextoAplicacao contexto) {
        Usuario usuario = contexto.getUsuario();
        List<ConfiguracaoAcessibilidade> configuracoes = new ArrayList<>();
        for (String tipo : ConfiguracaoAcessibilidade.TIPOS) {
            usuario.getConfiguracao(tipo).ifPresent(configuracoes::add);
        }
        List<String> linhas = new ArrayList<>();
        for (int i = 0; i < configuracoes.size(); i++) {
            linhas.add((i + 1) + " - " + configuracoes.get(i).resumo());
        }
        contexto.getSaida().lista("Configurações de acessibilidade", linhas);

        int escolha = contexto.perguntarNumero("Qual configuração deseja mudar? Diga zero para voltar.");
        if (escolha == 0) {
            return;
        }
        if (escolha < 1 || escolha > configuracoes.size()) {
            contexto.getSaida().erro("Opção inválida.");
            return;
        }
        ConfiguracaoAcessibilidade escolhida = configuracoes.get(escolha - 1);
        String novoValor;
        if (ConfiguracaoAcessibilidade.VELOCIDADE_FALA.equals(escolhida.getTipo())) {
            novoValor = String.valueOf(contexto.perguntarNumero(
                    "Nova velocidade da fala, de zero (bem lenta) a dez (bem rápida). Cinco é o normal."));
        } else {
            boolean ligar = contexto.perguntarSimNao("Ligar " + escolhida.nomeAmigavel() + "?");
            novoValor = ligar ? "sim" : "nao";
            if (ligar && !contexto.isVozDisponivel()) {
                contexto.getSaida().erro("Este computador não tem os recursos de voz do Windows; "
                        + "a configuração será salva, mas só funcionará no Windows.");
            }
        }
        ConfiguracaoAcessibilidade salva =
                contexto.getUsuarioService().alterarConfiguracao(usuario, escolhida.getTipo(), novoValor);
        contexto.aplicarPreferencias();
        contexto.getSaida().mensagem("Configuração salva. " + salva.resumo() + ".");
    }
}
