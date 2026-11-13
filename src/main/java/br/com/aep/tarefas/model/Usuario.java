package br.com.aep.tarefas.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Usuário do sistema (tabela USUARIO).
 * Composição 1:N: o usuário é dono das suas tarefas e das suas configurações
 * de acessibilidade; elas não existem sem ele (no banco: ON DELETE CASCADE).
 */
public class Usuario extends EntidadeBase {

    private static final Pattern FORMATO_EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private String nome;
    private String email;
    private final List<Tarefa> tarefas = new ArrayList<>();
    private final List<ConfiguracaoAcessibilidade> configuracoes = new ArrayList<>();

    public Usuario() {
    }

    public Usuario(String nome, String email) {
        this.nome = nome;
        setEmail(email);
    }

    /** Adiciona uma tarefa ao usuário e a vincula a ele (RN01). */
    public void adicionarTarefa(Tarefa tarefa) {
        tarefa.setIdUsuario(getId());
        tarefas.add(tarefa);
    }

    public void removerTarefa(int idTarefa) {
        tarefas.removeIf(t -> t.getId() == idTarefa);
    }

    /** Lista somente leitura das tarefas carregadas do banco. */
    public List<Tarefa> listarTarefas() {
        return Collections.unmodifiableList(tarefas);
    }

    public void substituirTarefas(List<Tarefa> novas) {
        tarefas.clear();
        novas.forEach(this::adicionarTarefa);
    }

    public void adicionarConfiguracao(ConfiguracaoAcessibilidade configuracao) {
        configuracao.setIdUsuario(getId());
        configuracoes.removeIf(c -> c.getTipo().equals(configuracao.getTipo()));
        configuracoes.add(configuracao);
    }

    public List<ConfiguracaoAcessibilidade> listarConfiguracoes() {
        return Collections.unmodifiableList(configuracoes);
    }

    public Optional<ConfiguracaoAcessibilidade> getConfiguracao(String tipo) {
        return configuracoes.stream().filter(c -> c.getTipo().equals(tipo)).findFirst();
    }

    public boolean configuracaoLigada(String tipo) {
        return getConfiguracao(tipo).map(ConfiguracaoAcessibilidade::isLigada).orElse(false);
    }

    @Override
    public void validar() {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("Informe o nome do usuário.");
        }
        if (nome.length() > 100) {
            throw new RegraNegocioException("O nome pode ter no máximo 100 caracteres.");
        }
        if (email == null || !FORMATO_EMAIL.matcher(email).matches()) {
            throw new RegraNegocioException("Informe um e-mail válido, por exemplo maria@email.com.");
        }
    }

    @Override
    public String resumo() {
        return "Usuário " + getId() + ": " + nome + " (" + email + ")";
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim().toLowerCase();
    }
}
