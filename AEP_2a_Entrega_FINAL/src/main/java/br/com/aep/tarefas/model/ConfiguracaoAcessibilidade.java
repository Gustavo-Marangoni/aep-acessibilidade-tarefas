package br.com.aep.tarefas.model;

import java.util.List;

/**
 * Configuração de acessibilidade do usuário (tabela CONFIGURACAO_ACESSIBILIDADE).
 * RF07 / RN06 - as configurações ficam vinculadas ao perfil do usuário.
 */
public class ConfiguracaoAcessibilidade extends EntidadeBase {

    /** Liga ou desliga a entrada por comandos de voz (RF08). Valores: sim / nao. */
    public static final String COMANDOS_DE_VOZ = "comandos_de_voz";
    /** Liga ou desliga a leitura das respostas em voz alta. Valores: sim / nao. */
    public static final String LEITURA_EM_VOZ = "leitura_em_voz";
    /** Velocidade da fala, de 0 (bem lenta) a 10 (bem rápida); 5 é o normal. */
    public static final String VELOCIDADE_FALA = "velocidade_fala";

    public static final List<String> TIPOS = List.of(COMANDOS_DE_VOZ, LEITURA_EM_VOZ, VELOCIDADE_FALA);

    private int idUsuario;
    private String tipo;
    private String valor;

    public ConfiguracaoAcessibilidade() {
    }

    public ConfiguracaoAcessibilidade(String tipo, String valor) {
        this.tipo = tipo;
        this.valor = valor;
    }

    /** Configurações criadas automaticamente quando um usuário se cadastra. */
    public static List<ConfiguracaoAcessibilidade> padroes() {
        return List.of(
                new ConfiguracaoAcessibilidade(COMANDOS_DE_VOZ, "nao"),
                new ConfiguracaoAcessibilidade(LEITURA_EM_VOZ, "sim"),
                new ConfiguracaoAcessibilidade(VELOCIDADE_FALA, "5"));
    }

    public void alterarConfiguracao(String novoValor) {
        String anterior = this.valor;
        this.valor = novoValor;
        try {
            validar();
        } catch (RegraNegocioException e) {
            this.valor = anterior;
            throw e;
        }
    }

    public boolean isLigada() {
        return "sim".equalsIgnoreCase(valor);
    }

    public int comoInteiro() {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            return 5;
        }
    }

    @Override
    public void validar() {
        if (!TIPOS.contains(tipo)) {
            throw new RegraNegocioException("Tipo de configuração desconhecido: " + tipo);
        }
        if (VELOCIDADE_FALA.equals(tipo)) {
            int velocidade;
            try {
                velocidade = Integer.parseInt(valor);
            } catch (NumberFormatException e) {
                throw new RegraNegocioException("A velocidade da fala deve ser um número de 0 a 10.");
            }
            if (velocidade < 0 || velocidade > 10) {
                throw new RegraNegocioException("A velocidade da fala deve ser um número de 0 a 10.");
            }
        } else if (!"sim".equals(valor) && !"nao".equals(valor)) {
            throw new RegraNegocioException("Use \"sim\" ou \"nao\" para " + nomeAmigavel() + ".");
        }
        if (idUsuario <= 0) {
            throw new RegraNegocioException("A configuração precisa estar vinculada a um usuário.");
        }
    }

    public String nomeAmigavel() {
        switch (tipo) {
            case COMANDOS_DE_VOZ:
                return "comandos de voz";
            case LEITURA_EM_VOZ:
                return "leitura em voz alta";
            case VELOCIDADE_FALA:
                return "velocidade da fala";
            default:
                return tipo;
        }
    }

    @Override
    public String resumo() {
        String valorFalado = "nao".equals(valor) ? "não" : valor;
        return nomeAmigavel() + ": " + valorFalado;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
