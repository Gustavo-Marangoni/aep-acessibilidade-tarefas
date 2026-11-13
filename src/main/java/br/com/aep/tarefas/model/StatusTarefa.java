package br.com.aep.tarefas.model;

/** RN03 - As tarefas poderão possuir os status "Pendente" ou "Concluída". */
public enum StatusTarefa {

    PENDENTE("Pendente"),
    CONCLUIDA("Concluída");

    private final String descricao;

    StatusTarefa(String descricao) {
        this.descricao = descricao;
    }

    /** Valor gravado na coluna tarefa.status. */
    public String getDescricao() {
        return descricao;
    }

    public static StatusTarefa deDescricao(String descricao) {
        for (StatusTarefa status : values()) {
            if (status.descricao.equalsIgnoreCase(descricao)) {
                return status;
            }
        }
        throw new RegraNegocioException("Status de tarefa inválido: " + descricao);
    }
}
