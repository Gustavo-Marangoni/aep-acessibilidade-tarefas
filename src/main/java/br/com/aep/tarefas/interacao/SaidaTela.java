package br.com.aep.tarefas.interacao;

import java.util.List;

/** Saída somente em texto, na tela. */
public class SaidaTela implements Saida {

    @Override
    public void mensagem(String texto) {
        System.out.println(texto);
    }

    @Override
    public void lista(String titulo, List<String> itens) {
        System.out.println();
        System.out.println("== " + titulo + " ==");
        if (itens.isEmpty()) {
            System.out.println("  (nenhum item)");
        }
        for (String item : itens) {
            System.out.println("  - " + item);
        }
        System.out.println();
    }

    @Override
    public void erro(String texto) {
        System.out.println("[!] " + texto);
    }
}
