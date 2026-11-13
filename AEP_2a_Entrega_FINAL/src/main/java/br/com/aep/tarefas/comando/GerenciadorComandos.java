package br.com.aep.tarefas.comando;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Guarda todos os comandos e descobre qual deles a frase do usuário aciona.
 * Trabalha somente com o tipo abstrato Comando: chamar executar() roda o
 * comportamento da subclasse certa (polimorfismo).
 */
public class GerenciadorComandos {

    private final List<Comando> comandos = new ArrayList<>();

    public GerenciadorComandos() {
        comandos.add(new ComandoCadastrarTarefa());
        comandos.add(new ComandoConsultarTarefa());
        comandos.add(new ComandoAlterarTarefa());
        comandos.add(new ComandoConcluirTarefa());
        comandos.add(new ComandoReabrirTarefa());
        comandos.add(new ComandoExcluirTarefa());
        comandos.add(new ComandoConfiguracoes());
        comandos.add(new ComandoMinhaConta());
        comandos.add(new ComandoAjuda(this::getComandos));
        comandos.add(new ComandoSair());
    }

    public Optional<Comando> interpretar(String frase) {
        return comandos.stream().filter(c -> c.reconhece(frase)).findFirst();
    }

    public List<Comando> getComandos() {
        return Collections.unmodifiableList(comandos);
    }

    /** Todas as frases que o reconhecedor de voz deve aceitar no menu. */
    public List<String> frasesDeVoz() {
        Set<String> frases = new LinkedHashSet<>();
        comandos.forEach(c -> frases.addAll(c.frasesDeVoz()));
        return new ArrayList<>(frases);
    }
}
