package br.com.aep.tarefas.interacao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;

/** Entrada tradicional pelo teclado (também usada como alternativa à voz). */
public class EntradaTeclado implements Entrada {

    private final BufferedReader leitor;

    public EntradaTeclado() {
        Charset charset = System.console() != null ? System.console().charset() : Charset.defaultCharset();
        this.leitor = new BufferedReader(new InputStreamReader(System.in, charset));
    }

    @Override
    public String ler(TipoResposta tipo) {
        System.out.print("> ");
        System.out.flush();
        try {
            String linha = leitor.readLine();
            return linha == null ? null : linha.trim();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public String getNome() {
        return "teclado";
    }
}
