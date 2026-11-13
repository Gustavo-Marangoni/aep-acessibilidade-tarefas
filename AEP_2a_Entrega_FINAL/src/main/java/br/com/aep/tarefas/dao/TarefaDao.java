package br.com.aep.tarefas.dao;

import br.com.aep.tarefas.model.StatusTarefa;
import br.com.aep.tarefas.model.Tarefa;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/** CRUD da tabela TAREFA (entidade principal do sistema). */
public class TarefaDao extends DaoBase<Tarefa> {

    public TarefaDao(ConexaoBanco conexaoBanco) {
        super(conexaoBanco);
    }

    @Override
    protected String getTabela() {
        return "tarefa";
    }

    @Override
    protected String getColunaId() {
        return "id_tarefa";
    }

    @Override
    protected Tarefa mapear(ResultSet linha) throws SQLException {
        Date data = linha.getDate("data");
        Tarefa tarefa = new Tarefa(linha.getString("titulo"), linha.getString("descricao"),
                data == null ? null : data.toLocalDate());
        tarefa.setId(linha.getInt("id_tarefa"));
        tarefa.setIdUsuario(linha.getInt("id_usuario"));
        tarefa.setStatus(StatusTarefa.deDescricao(linha.getString("status")));
        return tarefa;
    }

    @Override
    public void inserir(Tarefa tarefa) {
        int id = executarInsercao(
                "INSERT INTO tarefa (id_usuario, titulo, descricao, data, status) VALUES (?, ?, ?, ?, ?)",
                tarefa.getIdUsuario(), tarefa.getTitulo(), tarefa.getDescricao(),
                paraSql(tarefa.getData()), tarefa.getStatus().getDescricao());
        tarefa.setId(id);
    }

    @Override
    public void atualizar(Tarefa tarefa) {
        executarAlteracao(
                "UPDATE tarefa SET titulo = ?, descricao = ?, data = ?, status = ? WHERE id_tarefa = ?",
                tarefa.getTitulo(), tarefa.getDescricao(), paraSql(tarefa.getData()),
                tarefa.getStatus().getDescricao(), tarefa.getId());
    }

    /** Tarefas de um usuário: pendentes primeiro, depois por data. */
    public List<Tarefa> listarPorUsuario(int idUsuario) {
        return consultar("SELECT * FROM tarefa WHERE id_usuario = ? "
                + "ORDER BY status = 'Concluída', data IS NULL, data, id_tarefa", idUsuario);
    }

    private static Date paraSql(LocalDate data) {
        return data == null ? null : Date.valueOf(data);
    }
}
