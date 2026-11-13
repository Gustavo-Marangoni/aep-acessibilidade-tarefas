package br.com.aep.tarefas.dao;

import br.com.aep.tarefas.model.ConfiguracaoAcessibilidade;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/** CRUD da tabela CONFIGURACAO_ACESSIBILIDADE. */
public class ConfiguracaoAcessibilidadeDao extends DaoBase<ConfiguracaoAcessibilidade> {

    public ConfiguracaoAcessibilidadeDao(ConexaoBanco conexaoBanco) {
        super(conexaoBanco);
    }

    @Override
    protected String getTabela() {
        return "configuracao_acessibilidade";
    }

    @Override
    protected String getColunaId() {
        return "id_configuracao";
    }

    @Override
    protected ConfiguracaoAcessibilidade mapear(ResultSet linha) throws SQLException {
        ConfiguracaoAcessibilidade configuracao = new ConfiguracaoAcessibilidade(
                linha.getString("tipo"), linha.getString("valor"));
        configuracao.setId(linha.getInt("id_configuracao"));
        configuracao.setIdUsuario(linha.getInt("id_usuario"));
        return configuracao;
    }

    @Override
    public void inserir(ConfiguracaoAcessibilidade configuracao) {
        int id = executarInsercao(
                "INSERT INTO configuracao_acessibilidade (id_usuario, tipo, valor) VALUES (?, ?, ?)",
                configuracao.getIdUsuario(), configuracao.getTipo(), configuracao.getValor());
        configuracao.setId(id);
    }

    @Override
    public void atualizar(ConfiguracaoAcessibilidade configuracao) {
        executarAlteracao("UPDATE configuracao_acessibilidade SET valor = ? WHERE id_configuracao = ?",
                configuracao.getValor(), configuracao.getId());
    }

    public List<ConfiguracaoAcessibilidade> listarPorUsuario(int idUsuario) {
        return consultar("SELECT * FROM configuracao_acessibilidade WHERE id_usuario = ? ORDER BY tipo",
                idUsuario);
    }

    @Override
    protected String mensagemDuplicado() {
        return "Essa configuração já existe para o usuário.";
    }
}
