package br.com.aep.tarefas.ui;

import br.com.aep.tarefas.comando.ContextoAplicacao;
import br.com.aep.tarefas.comando.OperacaoCanceladaException;
import br.com.aep.tarefas.dao.DaoException;
import br.com.aep.tarefas.interacao.TipoResposta;
import br.com.aep.tarefas.model.ConfiguracaoAcessibilidade;
import br.com.aep.tarefas.model.RegraNegocioException;
import br.com.aep.tarefas.model.Usuario;
import br.com.aep.tarefas.util.Texto;

import java.util.List;

/** Tela antes do login: entrar, cadastrar usuário (RF01), listar usuários, fechar. */
public class TelaInicial {

    private static final List<String> FRASES = List.of(
            "entrar", "um", "cadastrar", "criar conta", "dois", "listar usuarios", "tres",
            "fechar programa", "zero", "cancelar");

    private final ContextoAplicacao contexto;

    public TelaInicial(ContextoAplicacao contexto) {
        this.contexto = contexto;
    }

    /** Mostra a tela até alguém entrar (devolve true) ou o programa ser fechado (false). */
    public boolean exibir() {
        while (contexto.isAplicacaoAtiva()) {
            contexto.setFrasesDeComando(() -> FRASES);
            contexto.getSaida().lista("Tela inicial", List.of(
                    "1 - Entrar",
                    "2 - Cadastrar novo usuário",
                    "3 - Listar usuários",
                    "0 - Fechar o programa"));
            try {
                String opcao = Texto.normalizar(contexto.perguntar("O que deseja fazer?", TipoResposta.COMANDO));
                switch (opcao) {
                    case "1", "um", "entrar" -> {
                        if (entrar()) {
                            return true;
                        }
                    }
                    case "2", "dois", "cadastrar", "criar conta" -> {
                        if (cadastrar()) {
                            return true;
                        }
                    }
                    case "3", "tres", "listar usuarios" -> listarUsuarios();
                    case "0", "zero", "fechar programa" -> contexto.encerrarAplicacao();
                    case "" -> { }
                    default -> contexto.getSaida().erro("Opção não reconhecida.");
                }
            } catch (OperacaoCanceladaException e) {
                if (contexto.isAplicacaoAtiva()) {
                    contexto.getSaida().mensagem(e.getMessage());
                }
            } catch (RegraNegocioException | DaoException e) {
                contexto.getSaida().erro(e.getMessage());
            }
        }
        return false;
    }

    private boolean entrar() {
        Usuario usuario;
        if (contexto.getEntrada().isVoz()) {
            listarUsuarios();
            int numero = contexto.perguntarNumero("Diga o número do seu usuário.");
            usuario = contexto.getUsuarioService().entrarPorId(numero);
        } else {
            String resposta = contexto.perguntar("Digite seu e-mail (ou o número do usuário):", TipoResposta.TEXTO_LIVRE);
            usuario = resposta.matches("\\d+")
                    ? contexto.getUsuarioService().entrarPorId(Integer.parseInt(resposta))
                    : contexto.getUsuarioService().entrar(resposta);
        }
        contexto.iniciarSessao(usuario);
        contexto.getSaida().mensagem("Olá, " + usuario.getNome() + "! Você entrou usando " + contexto.getEntrada().getNome() + ".");
        return true;
    }

    private boolean cadastrar() {
        String nome = contexto.perguntar("Qual o seu nome?", TipoResposta.TEXTO_LIVRE);
        String email = contexto.perguntar("Qual o seu e-mail?", TipoResposta.TEXTO_LIVRE);
        Usuario usuario = contexto.getUsuarioService().cadastrar(nome, email);
        contexto.getSaida().mensagem("Usuário cadastrado com o número " + usuario.getId() + ".");
        if (contexto.isVozDisponivel()
                && contexto.perguntarSimNao("Deseja ligar os comandos de voz agora?")) {
            contexto.getUsuarioService().alterarConfiguracao(usuario,
                    ConfiguracaoAcessibilidade.COMANDOS_DE_VOZ, "sim");
        }
        contexto.iniciarSessao(usuario);
        contexto.getSaida().mensagem("Bem-vindo, " + usuario.getNome() + "!");
        return true;
    }

    private void listarUsuarios() {
        List<String> linhas = contexto.getUsuarioService().listar().stream()
                .map(u -> u.getId() + " - " + u.getNome())
                .toList();
        contexto.getSaida().lista("Usuários cadastrados", linhas);
    }
}
