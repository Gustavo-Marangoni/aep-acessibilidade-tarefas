package br.com.aep.tarefas.interacao;

/**
 * Forma pela qual o usuário fala com o sistema. Há duas implementações:
 * EntradaTeclado e EntradaVoz (RF08). O resto do sistema não sabe qual
 * está em uso - basta chamar ler() (polimorfismo).
 */
public interface Entrada {

    /**
     * Lê uma resposta do usuário.
     *
     * @return o texto lido; vazio quando nada foi entendido; null quando a
     *         entrada foi encerrada (fim do teclado)
     */
    String ler(TipoResposta tipo);

    /** Nome exibido para o usuário, por exemplo "teclado" ou "voz". */
    String getNome();

    default boolean isVoz() {
        return false;
    }
}
