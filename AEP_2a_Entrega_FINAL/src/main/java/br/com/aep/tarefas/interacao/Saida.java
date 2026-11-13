package br.com.aep.tarefas.interacao;

import java.util.List;

/**
 * Forma pela qual o sistema responde ao usuário. A implementação muda
 * conforme a configuração "leitura em voz alta" do usuário.
 */
public interface Saida {

    /** Mostra (e, se for o caso, fala) uma mensagem. */
    void mensagem(String texto);

    /** Mostra uma lista com título. */
    void lista(String titulo, List<String> itens);

    /** Mostra um aviso de erro. */
    void erro(String texto);
}
