package br.com.aep.tarefas.dao;

import br.com.aep.tarefas.model.EntidadeBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Classe abstrata que implementa as partes do CRUD que são iguais para
 * todas as tabelas (buscar por id, listar, excluir, gerar id). Cada DAO
 * concreto sobrescreve apenas o que é específico da sua tabela (polimorfismo).
 */
public abstract class DaoBase<T extends EntidadeBase> implements Dao<T> {

    protected final ConexaoBanco conexaoBanco;

    protected DaoBase(ConexaoBanco conexaoBanco) {
        this.conexaoBanco = conexaoBanco;
    }

    /** Nome da tabela no banco. */
    protected abstract String getTabela();

    /** Nome da coluna de chave primária. */
    protected abstract String getColunaId();

    /** Converte a linha atual do ResultSet em objeto. */
    protected abstract T mapear(ResultSet linha) throws SQLException;

    /** Ordenação usada nas listagens. */
    protected String getOrdenacao() {
        return getColunaId();
    }

    @Override
    public Optional<T> buscarPorId(int id) {
        String sql = "SELECT * FROM " + getTabela() + " WHERE " + getColunaId() + " = ?";
        List<T> resultado = consultar(sql, id);
        return resultado.isEmpty() ? Optional.empty() : Optional.of(resultado.get(0));
    }

    @Override
    public List<T> listarTodos() {
        return consultar("SELECT * FROM " + getTabela() + " ORDER BY " + getOrdenacao());
    }

    @Override
    public boolean excluir(int id) {
        String sql = "DELETE FROM " + getTabela() + " WHERE " + getColunaId() + " = ?";
        return executarAlteracao(sql, id) > 0;
    }

    /** Executa um SELECT com parâmetros e mapeia todas as linhas. */
    protected List<T> consultar(String sql, Object... parametros) {
        try (Connection conexao = conexaoBanco.abrir();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            preencher(comando, parametros);
            try (ResultSet linhas = comando.executeQuery()) {
                List<T> lista = new ArrayList<>();
                while (linhas.next()) {
                    lista.add(mapear(linhas));
                }
                return lista;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao consultar " + getTabela() + ": " + e.getMessage(), e);
        }
    }

    /** Executa INSERT e devolve a chave gerada pelo AUTO_INCREMENT. */
    protected int executarInsercao(String sql, Object... parametros) {
        try (Connection conexao = conexaoBanco.abrir();
             PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(comando, parametros);
            comando.executeUpdate();
            try (ResultSet chaves = comando.getGeneratedKeys()) {
                if (chaves.next()) {
                    return chaves.getInt(1);
                }
            }
            throw new DaoException("O banco não devolveu o id gerado em " + getTabela() + ".", null);
        } catch (SQLException e) {
            throw traduzir(e);
        }
    }

    /** Executa UPDATE/DELETE e devolve quantas linhas foram afetadas. */
    protected int executarAlteracao(String sql, Object... parametros) {
        try (Connection conexao = conexaoBanco.abrir();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            preencher(comando, parametros);
            return comando.executeUpdate();
        } catch (SQLException e) {
            throw traduzir(e);
        }
    }

    private void preencher(PreparedStatement comando, Object... parametros) throws SQLException {
        for (int i = 0; i < parametros.length; i++) {
            comando.setObject(i + 1, parametros[i]);
        }
    }

    private DaoException traduzir(SQLException e) {
        // 1062 = chave única duplicada no MySQL.
        if (e.getErrorCode() == 1062) {
            return new DaoException(mensagemDuplicado(), e);
        }
        return new DaoException("Erro ao gravar em " + getTabela() + ": " + e.getMessage(), e);
    }

    /** Mensagem para violação de chave única; cada DAO pode personalizar. */
    protected String mensagemDuplicado() {
        return "Já existe um registro com esses dados.";
    }
}
