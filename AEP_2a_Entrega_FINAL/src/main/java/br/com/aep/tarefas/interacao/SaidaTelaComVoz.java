package br.com.aep.tarefas.interacao;

import java.util.List;

/**
 * Saída na tela + leitura em voz alta. Herda de SaidaTela e sobrescreve os
 * métodos para acrescentar a fala (herança + polimorfismo).
 */
public class SaidaTelaComVoz extends SaidaTela {

    private final MotorVozWindows motor;
    private final int velocidade;

    public SaidaTelaComVoz(MotorVozWindows motor, int velocidade) {
        this.motor = motor;
        this.velocidade = velocidade;
    }

    @Override
    public void mensagem(String texto) {
        super.mensagem(texto);
        motor.falar(paraFala(texto), velocidade);
    }

    @Override
    public void lista(String titulo, List<String> itens) {
        super.lista(titulo, itens);
        StringBuilder fala = new StringBuilder(titulo).append(". ");
        if (itens.isEmpty()) {
            fala.append("Nenhum item.");
        }
        for (String item : itens) {
            fala.append(item).append(". ");
        }
        motor.falar(paraFala(fala.toString()), velocidade);
    }

    @Override
    public void erro(String texto) {
        super.erro(texto);
        motor.falar("Atenção. " + paraFala(texto), velocidade);
    }

    /** Remove símbolos que atrapalham a leitura em voz alta. */
    private static String paraFala(String texto) {
        return texto.replace("[!]", "").replace("==", "").replace(">", "").replace("\"", "");
    }
}
