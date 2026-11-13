package br.com.aep.tarefas.interacao;

import br.com.aep.tarefas.util.Texto;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * RF08 - Entrada por comandos de voz. Para cada tipo de resposta monta uma
 * lista fechada de frases (o que aumenta muito a taxa de acerto); para textos
 * livres, como o título de uma tarefa, usa o modo ditado do Windows.
 */
public class EntradaVoz implements Entrada {

    private static final int SEGUNDOS_ESCUTA = 8;
    private static final List<String> FRASES_SIM_NAO = List.of("sim", "nao", "confirmar", "cancelar");
    private static final List<String> FRASES_DATA = List.of("hoje", "amanha", "depois de amanha",
            "sem data", "manter", "cancelar");

    private final MotorVozWindows motor;
    private final Supplier<List<String>> frasesDeComando;

    /**
     * @param frasesDeComando fornece as frases que valem como comando no
     *                        momento (vêm do GerenciadorComandos)
     */
    public EntradaVoz(MotorVozWindows motor, Supplier<List<String>> frasesDeComando) {
        this.motor = motor;
        this.frasesDeComando = frasesDeComando;
    }

    @Override
    public String ler(TipoResposta tipo) {
        System.out.println("  [ouvindo... fale agora]");
        String reconhecido = motor.ouvir(frasesPara(tipo), SEGUNDOS_ESCUTA);
        if (reconhecido.isEmpty()) {
            System.out.println("  [nada reconhecido]");
        } else {
            System.out.println("  [você disse: " + reconhecido + "]");
        }
        return reconhecido;
    }

    private List<String> frasesPara(TipoResposta tipo) {
        switch (tipo) {
            case COMANDO:
                return frasesDeComando.get();
            case NUMERO:
                List<String> numeros = new ArrayList<>(Texto.frasesDeNumero());
                numeros.add("cancelar");
                return numeros;
            case SIM_NAO:
                return FRASES_SIM_NAO;
            case DATA:
                return FRASES_DATA;
            case TEXTO_LIVRE:
            default:
                return List.of(); // lista vazia = ditado livre
        }
    }

    @Override
    public String getNome() {
        return "voz";
    }

    @Override
    public boolean isVoz() {
        return true;
    }
}
