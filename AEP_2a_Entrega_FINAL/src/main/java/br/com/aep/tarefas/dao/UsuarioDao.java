package br.com.aep.tarefas.dao;

import br.com.aep.tarefas.model.Usuario;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** CRUD da tabela USUARIO. */
public class UsuarioDao extends DaoBase<Usuario> {

    public UsuarioDao(ConexaoBanco conexaoBanco) {
        super(conexaoBanco);
    }

    @Override
    protected String getTabela() {
        return "usuario";
    }

    @Override
    protected String getColunaId() {
        return "id_usuario";
    }

    @Override
    protected String getOrdenacao() {
        return "nome";
    }

    @Override
    protected Usuario mapear(ResultSet linha) throws SQLException {
        Usuario usuario = new Usuario(linha.getString("nome"), linha.getString("email"));
        usuario.setId(linha.getInt("id_usuario"));
        return usuario;
    }

    @Override
    public void inserir(Usuario usuario) {
        int id = executarInsercao("INSERT INTO usuario (nome, email) VALUES (?, ?)",
                usuario.getNome(), usuario.getEmail());
        usuario.setId(id);
    }

    @Override
    public void atualizar(Usuario usuario) {
        executarAlteracao("UPDATE usuario SET nome = ?, email = ? WHERE id_usuario = ?",
                usuario.getNome(), usuario.getEmail(), usuario.getId());
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        List<Usuario> encontrados = consultar("SELECT * FROM usuario WHERE email = ?",
                email == null ? null : email.trim().toLowerCase());
        return encontrados.isEmpty() ? Optional.empty() : Optional.of(encontrados.get(0));
    }

    @Override
    protected String mensagemDuplicado() {
        return "Já existe um usuário cadastrado com esse e-mail.";
    }
}
