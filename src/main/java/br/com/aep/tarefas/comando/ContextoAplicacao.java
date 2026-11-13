package br.com.aep.tarefas.comando;

import br.com.aep.tarefas.interacao.Entrada;
import br.com.aep.tarefas.interacao.EntradaTeclado;
import br.com.aep.tarefas.interacao.EntradaVoz;
import br.com.aep.tarefas.interacao.MotorVozWindows;
import br.com.aep.tarefas.interacao.Saida;
import br.com.aep.tarefas.interacao.SaidaTela;
import br.com.aep.tarefas.interacao.SaidaTelaComVoz;
import br.com.aep.tarefas.interacao.TipoResposta;
import br.com.aep.tarefas.model.ConfiguracaoAcessibilidade;
import br.com.aep.tarefas.model.RegraNegocioException;
import br.com.aep.tarefas.model.Usuario;
import br.com.aep.tarefas.service.TarefaService;
import br.com.aep.tarefas.service.UsuarioService;
import br.com.aep.tarefas.util.Texto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Tudo o que um comando precisa para trabalhar: usuário logado, serviços,
 * e a entrada/saída atuais (teclado ou voz). Também concentra as perguntas
 * mais comuns (número, sim/não, data), já tratando "cancelar" e falhas de voz.
 */
public class ContextoAplicacao {

    private static final int TENTATIVAS_POR_VOZ = 3;

    private final UsuarioService usuarioService;
    private final TarefaService tarefaService;
    private final MotorVozWindows motorVoz;
    private final EntradaTeclado teclado;
    private Supplier<List<String>> frasesDeComando = List::of;

    private Entrada entrada;
    private Saida saida;
    private Usuario usuario;
    private boolean sessaoAtiva;
    private boolean aplicacaoAtiva = true;

    public ContextoAplicacao(UsuarioService usuarioService, TarefaService tarefaService,
                             MotorVozWindows motorVoz, EntradaTeclado teclado) {
        this.usuarioService = usuarioService;
        this.tarefaService = tarefaService;
        this.motorVoz = motorVoz;
        this.teclado = teclado;
        this.entrada = teclado;
        this.saida = new SaidaTela();
    }

    // ------------------------------------------------------------------
    // Sessão e preferências de acessibilidade
    // ------------------------------------------------------------------

    public void iniciarSessao(Usuario usuarioLogado) {
        this.usuario = usuarioLogado;
        this.sessaoAtiva = true;
        aplicarPreferencias();
    }

    public void encerrarSessao() {
        this.usuario = null;
        this.sessaoAtiva = false;
    }

    /** RF07 - aplica as configurações de acessibilidade do usuário logado. */
    public void aplicarPreferencias() {
        if (usuario == null) {
            return;
        }
        boolean querVoz = usuario.configuracaoLigada(ConfiguracaoAcessibilidade.COMANDOS_DE_VOZ);
        boolean querLeitura = usuario.configuracaoLigada(ConfiguracaoAcessibilidade.LEITURA_EM_VOZ);
        int velocidade = usuario.getConfiguracao(ConfiguracaoAcessibilidade.VELOCIDADE_FALA)
                .map(ConfiguracaoAcessibilidade::comoInteiro).orElse(5);
        usarModo(querVoz, querLeitura, velocidade);
    }

    /** Liga/desliga voz na entrada e na saída (sem depender de usuário logado). */
    public void usarModo(boolean entradaPorVoz, boolean leituraEmVoz, int velocidade) {
        boolean vozDisponivel = motorVoz.isDisponivel();
        saida = (leituraEmVoz && vozDisponivel) ? new SaidaTelaComVoz(motorVoz, velocidade) : new SaidaTela();
        entrada = (entradaPorVoz && vozDisponivel) ? new EntradaVoz(motorVoz, () -> frasesDeComando.get()) : teclado;
        if ((entradaPorVoz || leituraEmVoz) && !vozDisponivel) {
            saida.erro("Recursos de voz indisponíveis neste computador (somente Windows). Usando o teclado.");
        }
    }

    // ------------------------------------------------------------------
    // Perguntas ao usuário
    // ------------------------------------------------------------------

    /** Pergunta algo e devolve a resposta. "cancelar" interrompe o comando. */
    public String perguntar(String pergunta, TipoResposta tipo) {
        saida.mensagem(pergunta);
        String resposta = lerComTolerancia(tipo);
        if (Texto.normalizar(resposta).equals("cancelar")) {
            throw new OperacaoCanceladaException();
        }
        return resposta;
    }

    /**
     * Lê uma resposta. Na voz, se nada for entendido algumas vezes seguidas,
     * oferece o teclado como alternativa para não travar o usuário.
     */
    public String lerComTolerancia(TipoResposta tipo) {
        if (!entrada.isVoz()) {
            return lerTeclado(tipo);
        }
        for (int tentativa = 1; tentativa <= TENTATIVAS_POR_VOZ; tentativa++) {
            String resposta;
            try {
                resposta = entrada.ler(tipo);
            } catch (IllegalStateException e) {
                saida.erro(e.getMessage() + " Voltando para o teclado.");
                entrada = teclado;
                return lerTeclado(tipo);
            }
            if (!resposta.isEmpty() || tipo == TipoResposta.TEXTO_LIVRE && tentativa == TENTATIVAS_POR_VOZ) {
                return resposta;
            }
            if (tentativa < TENTATIVAS_POR_VOZ) {
                saida.mensagem("Não entendi. Pode repetir?");
            }
        }
        saida.mensagem("Não consegui entender pela voz. Digite a resposta, por favor.");
        return lerTeclado(tipo);
    }

    private String lerTeclado(TipoResposta tipo) {
        String linha = teclado.ler(tipo);
        if (linha == null) {
            // Fim da entrada (Ctrl+Z / Ctrl+D): encerra o programa com segurança.
            encerrarAplicacao();
            throw new OperacaoCanceladaException();
        }
        return linha;
    }

    public int perguntarNumero(String pergunta) {
        while (true) {
            String resposta = perguntar(pergunta, TipoResposta.NUMERO);
            Optional<Integer> numero = Texto.paraNumero(resposta);
            if (numero.isPresent()) {
                return numero.get();
            }
            saida.erro("Responda com um número.");
        }
    }

    public boolean perguntarSimNao(String pergunta) {
        while (true) {
            String resposta = perguntar(pergunta + " (sim ou não)", TipoResposta.SIM_NAO);
            if (Texto.ehSim(resposta)) {
                return true;
            }
            if (Texto.ehNao(resposta)) {
                return false;
            }
            saida.erro("Responda sim ou não.");
        }
    }

    /** Pergunta uma data; devolve null para "sem data". */
    public LocalDate perguntarData(String pergunta) {
        while (true) {
            String resposta = perguntar(pergunta, TipoResposta.DATA);
            try {
                return Texto.paraData(resposta, LocalDate.now());
            } catch (RegraNegocioException e) {
                saida.erro(e.getMessage());
            }
        }
    }

    // ------------------------------------------------------------------
    // Acessores
    // ------------------------------------------------------------------

    public Usuario getUsuario() {
        if (usuario == null) {
            throw new RegraNegocioException("Nenhum usuário entrou no sistema.");
        }
        return usuario;
    }

    public boolean temUsuario() {
        return usuario != null;
    }

    public UsuarioService getUsuarioService() {
        return usuarioService;
    }

    public TarefaService getTarefaService() {
        return tarefaService;
    }

    public Entrada getEntrada() {
        return entrada;
    }

    public Saida getSaida() {
        return saida;
    }

    public boolean isSessaoAtiva() {
        return sessaoAtiva;
    }

    public boolean isAplicacaoAtiva() {
        return aplicacaoAtiva;
    }

    public void encerrarAplicacao() {
        aplicacaoAtiva = false;
        sessaoAtiva = false;
    }

    public void setFrasesDeComando(Supplier<List<String>> frasesDeComando) {
        this.frasesDeComando = frasesDeComando;
    }

    public boolean isVozDisponivel() {
        return motorVoz.isDisponivel();
    }
}
