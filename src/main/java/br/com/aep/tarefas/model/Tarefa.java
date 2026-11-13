package br.com.aep.tarefas.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Tarefa de um usuário (tabela TAREFA). Faz parte da composição
 * Usuario 1 ---- 0..* Tarefa.
 */
public class Tarefa extends EntidadeBase {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final int TAMANHO_MAXIMO_TITULO = 120;
    public static final int TAMANHO_MAXIMO_DESCRICAO = 500;

    private int idUsuario;
    private String titulo;
    private String descricao;
    private LocalDate data;
    private StatusTarefa status = StatusTarefa.PENDENTE;

    public Tarefa() {
    }

    public Tarefa(String titulo, String descricao, LocalDate data) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.data = data;
    }

    /** RF06 - marca a tarefa como "Concluída". */
    public void concluir() {
        if (status == StatusTarefa.CONCLUIDA) {
            throw new RegraNegocioException("A tarefa \"" + titulo + "\" já está concluída.");
        }
        status = StatusTarefa.CONCLUIDA;
    }

    /** RF06 - volta a tarefa para "Pendente". */
    public void reabrir() {
        if (status == StatusTarefa.PENDENTE) {
            throw new RegraNegocioException("A tarefa \"" + titulo + "\" já está pendente.");
        }
        status = StatusTarefa.PENDENTE;
    }

    /** RF04 - altera os dados da tarefa e valida novamente. */
    public void alterar(String novoTitulo, String novaDescricao, LocalDate novaData) {
        this.titulo = novoTitulo;
        this.descricao = novaDescricao;
        this.data = novaData;
        validar();
    }

    @Override
    public void validar() {
        // RN02 - toda tarefa deverá possuir pelo menos um título e um status.
        if (titulo == null || titulo.isBlank()) {
            throw new RegraNegocioException("A tarefa precisa de um título.");
        }
        if (titulo.length() > TAMANHO_MAXIMO_TITULO) {
            throw new RegraNegocioException("O título pode ter no máximo " + TAMANHO_MAXIMO_TITULO + " caracteres.");
        }
        if (descricao != null && descricao.length() > TAMANHO_MAXIMO_DESCRICAO) {
            throw new RegraNegocioException("A descrição pode ter no máximo " + TAMANHO_MAXIMO_DESCRICAO + " caracteres.");
        }
        if (status == null) {
            throw new RegraNegocioException("A tarefa precisa de um status.");
        }
        if (idUsuario <= 0) {
            // RN01 - a tarefa sempre pertence a um usuário.
            throw new RegraNegocioException("A tarefa precisa estar vinculada a um usuário.");
        }
    }

    @Override
    public String resumo() {
        StringBuilder texto = new StringBuilder();
        texto.append("Tarefa ").append(getId()).append(": ").append(titulo)
             .append(" (").append(status.getDescricao()).append(")");
        if (data != null) {
            texto.append(" para ").append(data.format(FORMATO_DATA));
        }
        return texto.toString();
    }

    public String detalhes() {
        String textoDescricao = (descricao == null || descricao.isBlank()) ? "sem descrição" : descricao;
        return resumo() + ". Descrição: " + textoDescricao + ".";
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public StatusTarefa getStatus() {
        return status;
    }

    public void setStatus(StatusTarefa status) {
        this.status = status;
    }

    public boolean isConcluida() {
        return status == StatusTarefa.CONCLUIDA;
    }
}
