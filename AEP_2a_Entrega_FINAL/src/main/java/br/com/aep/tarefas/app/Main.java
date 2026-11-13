package br.com.aep.tarefas.app;

import br.com.aep.tarefas.comando.ContextoAplicacao;
import br.com.aep.tarefas.comando.GerenciadorComandos;
import br.com.aep.tarefas.dao.ConexaoBanco;
import br.com.aep.tarefas.dao.ConfiguracaoAcessibilidadeDao;
import br.com.aep.tarefas.dao.DaoException;
import br.com.aep.tarefas.dao.TarefaDao;
import br.com.aep.tarefas.dao.UsuarioDao;
import br.com.aep.tarefas.interacao.EntradaTeclado;
import br.com.aep.tarefas.interacao.MotorVozWindows;
import br.com.aep.tarefas.service.TarefaService;
import br.com.aep.tarefas.service.UsuarioService;
import br.com.aep.tarefas.ui.TelaInicial;
import br.com.aep.tarefas.ui.TelaPrincipal;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Ponto de entrada da aplicação "Tarefas Acessíveis".
 *
 * Argumentos opcionais:
 *   --voz   já começa a tela inicial ouvindo comandos de voz (Windows)
 */
public class Main {

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        boolean iniciarComVoz = Arrays.asList(args).contains("--voz");

        System.out.println("==========================================");
        System.out.println("  TAREFAS ACESSÍVEIS - AEP / ODS 10");
        System.out.println("  Gerenciador de tarefas com comandos de voz");
        System.out.println("==========================================");

        ConexaoBanco conexao = ConexaoBanco.getInstancia();
        try {
            conexao.testar();
            System.out.println("Banco de dados conectado: " + conexao.getUrl());
        } catch (DaoException e) {
            System.out.println("[!] " + e.getMessage());
            System.out.println("Veja a seção \"Como executar\" do README.md.");
            System.exit(1);
            return;
        }

        UsuarioDao usuarioDao = new UsuarioDao(conexao);
        TarefaDao tarefaDao = new TarefaDao(conexao);
        ConfiguracaoAcessibilidadeDao configuracaoDao = new ConfiguracaoAcessibilidadeDao(conexao);

        ContextoAplicacao contexto = new ContextoAplicacao(
                new UsuarioService(usuarioDao, tarefaDao, configuracaoDao),
                new TarefaService(tarefaDao),
                new MotorVozWindows(),
                new EntradaTeclado());
        if (iniciarComVoz) {
            contexto.usarModo(true, true, 5);
        }

        TelaInicial telaInicial = new TelaInicial(contexto);
        GerenciadorComandos gerenciador = new GerenciadorComandos();
        while (telaInicial.exibir()) {
            new TelaPrincipal(contexto, gerenciador).exibir();
            if (!iniciarComVoz) {
                contexto.usarModo(false, false, 5);
            }
        }
        System.out.println("Programa encerrado.");
    }
}
