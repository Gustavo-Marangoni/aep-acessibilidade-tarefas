package br.com.aep.tarefas.comando;

import br.com.aep.tarefas.util.Texto;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe abstrata de todos os comandos do sistema (diagrama de classes da
 * 1ª entrega). Cada subclasse sobrescreve executar() com o seu comportamento
 * (polimorfismo via @Override). Um comando pode ser acionado pelo número do
 * menu, digitando a frase ou falando a frase (RF08).
 */
public abstract class Comando {

    private final int numero;
    private final String descricao;
    private final List<String> frases;

    /**
     * @param numero    número da opção no menu
     * @param descricao texto exibido no menu
     * @param frases    frases que acionam o comando (sem acento, minúsculas)
     */
    protected Comando(int numero, String descricao, String... frases) {
        this.numero = numero;
        this.descricao = descricao;
        this.frases = List.of(frases);
    }

    /** Executa o comando. Cada tipo de comando implementa do seu jeito. */
    public abstract void executar(ContextoAplicacao contexto);

    /** Verdadeiro se a frase (digitada ou falada) aciona este comando. */
    public boolean reconhece(String fraseRecebida) {
        String frase = Texto.normalizar(fraseRecebida);
        if (frase.isEmpty()) {
            return false;
        }
        if (frase.equals(String.valueOf(numero)) || frase.equals(Texto.porExtenso(numero))
                || frase.equals("opcao " + Texto.porExtenso(numero))) {
            return true;
        }
        for (String gatilho : frases) {
            if (gatilho.equals(frase) || (frase.length() >= 4 && gatilho.startsWith(frase + " "))) {
                return true;
            }
        }
        return false;
    }

    /** Frases aceitas pelo reconhecedor de voz para este comando. */
    public List<String> frasesDeVoz() {
        List<String> todas = new ArrayList<>(frases);
        todas.add(Texto.porExtenso(numero));
        todas.add("opcao " + Texto.porExtenso(numero));
        return todas;
    }

    public String linhaDoMenu() {
        return numero + " - " + descricao + "  (diga: \"" + frases.get(0) + "\")";
    }

    public int getNumero() {
        return numero;
    }

    public String getDescricao() {
        return descricao;
    }
}
