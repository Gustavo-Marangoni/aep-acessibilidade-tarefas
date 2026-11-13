package br.com.aep.tarefas.dao;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Fábrica de conexões com o MySQL.
 *
 * Lê as configurações do arquivo db.properties na raiz do projeto
 * (copie o db.properties.example). Variáveis de ambiente AEP_DB_URL,
 * AEP_DB_USUARIO e AEP_DB_SENHA têm prioridade sobre o arquivo.
 */
public final class ConexaoBanco {

    private static final String ARQUIVO_CONFIGURACAO = "db.properties";
    private static final String URL_PADRAO =
            "jdbc:mysql://localhost:3306/aep_tarefas?useSSL=false&allowPublicKeyRetrieval=true"
            + "&serverTimezone=America/Sao_Paulo&characterEncoding=UTF-8";

    private static ConexaoBanco instancia;

    private final String url;
    private final String usuario;
    private final String senha;

    private ConexaoBanco() {
        Properties propriedades = new Properties();
        Path arquivo = Path.of(ARQUIVO_CONFIGURACAO);
        if (Files.exists(arquivo)) {
            try (InputStream entrada = Files.newInputStream(arquivo)) {
                propriedades.load(entrada);
            } catch (IOException e) {
                throw new DaoException("Não foi possível ler o arquivo " + ARQUIVO_CONFIGURACAO + ".", e);
            }
        }
        this.url = valor("AEP_DB_URL", propriedades.getProperty("db.url", URL_PADRAO));
        this.usuario = valor("AEP_DB_USUARIO", propriedades.getProperty("db.usuario", "root"));
        this.senha = valor("AEP_DB_SENHA", propriedades.getProperty("db.senha", ""));
    }

    private static String valor(String variavelAmbiente, String padrao) {
        String doAmbiente = System.getenv(variavelAmbiente);
        return (doAmbiente == null || doAmbiente.isBlank()) ? padrao : doAmbiente;
    }

    public static synchronized ConexaoBanco getInstancia() {
        if (instancia == null) {
            instancia = new ConexaoBanco();
        }
        return instancia;
    }

    public Connection abrir() {
        try {
            return DriverManager.getConnection(url, usuario, senha);
        } catch (SQLException e) {
            throw new DaoException(explicarFalha(e), e);
        }
    }

    /** Abre e fecha uma conexão para confirmar que o banco está acessível. */
    public void testar() {
        try (Connection conexao = abrir()) {
            if (!conexao.isValid(3)) {
                throw new DaoException("A conexão com o MySQL não respondeu.", null);
            }
        } catch (SQLException e) {
            throw new DaoException(explicarFalha(e), e);
        }
    }

    private String explicarFalha(SQLException e) {
        String mensagem = e.getMessage() == null ? "" : e.getMessage();
        if (mensagem.contains("No suitable driver")) {
            return "Driver JDBC do MySQL não encontrado. Confira se o Maven conseguiu baixar as dependências (mvn clean compile).";
        }
        if (mensagem.contains("Unknown database")) {
            return "O banco aep_tarefas não existe. Rode o arquivo database/script.sql no MySQL.";
        }
        if (mensagem.contains("Access denied")) {
            return "Usuário ou senha do MySQL incorretos. Ajuste o arquivo db.properties.";
        }
        if (mensagem.contains("Communications link failure") || mensagem.contains("Connection refused")) {
            return "Não foi possível falar com o MySQL em " + url
                    + ". Verifique se o serviço do MySQL está ligado.";
        }
        return "Erro ao conectar no MySQL: " + mensagem;
    }

    public String getUrl() {
        return url;
    }
}
