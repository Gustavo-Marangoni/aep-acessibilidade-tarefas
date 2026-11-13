package br.com.aep.tarefas.model;

/**
 * Classe abstrata base de todas as entidades persistidas no banco.
 * Concentra o identificador (chave primária) e obriga cada entidade
 * a implementar sua própria validação e seu próprio resumo (polimorfismo).
 */
public abstract class EntidadeBase {

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isNova() {
        return id == 0;
    }

    /**
     * Verifica as regras de negócio da entidade.
     *
     * @throws RegraNegocioException quando alguma regra não é atendida
     */
    public abstract void validar();

    /** Texto curto usado para exibir e ler em voz alta a entidade. */
    public abstract String resumo();

    @Override
    public String toString() {
        return resumo();
    }
}
