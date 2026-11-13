package br.com.aep.tarefas.service;

import br.com.aep.tarefas.dao.ConfiguracaoAcessibilidadeDao;
import br.com.aep.tarefas.dao.TarefaDao;
import br.com.aep.tarefas.dao.UsuarioDao;
import br.com.aep.tarefas.model.ConfiguracaoAcessibilidade;
import br.com.aep.tarefas.model.RegraNegocioException;
import br.com.aep.tarefas.model.Usuario;

import java.util.List;

/** Regras de negócio de usuário (RF01) e de suas configurações (RF07). */
public class UsuarioService {

    private final UsuarioDao usuarioDao;
    private final TarefaDao tarefaDao;
    private final ConfiguracaoAcessibilidadeDao configuracaoDao;

    public UsuarioService(UsuarioDao usuarioDao, TarefaDao tarefaDao, ConfiguracaoAcessibilidadeDao configuracaoDao) {
        this.usuarioDao = usuarioDao;
        this.tarefaDao = tarefaDao;
        this.configuracaoDao = configuracaoDao;
    }

    /** RF01 - cadastra o usuário e cria as configurações de acessibilidade padrão. */
    public Usuario cadastrar(String nome, String email) {
        Usuario usuario = new Usuario(limpar(nome), email);
        usuario.validar();
        if (usuarioDao.buscarPorEmail(usuario.getEmail()).isPresent()) {
            throw new RegraNegocioException("Já existe um usuário cadastrado com esse e-mail.");
        }
        usuarioDao.inserir(usuario);
        for (ConfiguracaoAcessibilidade padrao : ConfiguracaoAcessibilidade.padroes()) {
            usuario.adicionarConfiguracao(padrao);
            configuracaoDao.inserir(padrao);
        }
        return usuario;
    }

    /** Busca o usuário pelo e-mail e carrega suas tarefas e configurações. */
    public Usuario entrar(String email) {
        Usuario usuario = usuarioDao.buscarPorEmail(email)
                .orElseThrow(() -> new RegraNegocioException("Nenhum usuário encontrado com o e-mail " + email + "."));
        carregarDependencias(usuario);
        return usuario;
    }

    /** Entrada pelo número do usuário (mais fácil de falar do que um e-mail). */
    public Usuario entrarPorId(int idUsuario) {
        Usuario usuario = usuarioDao.buscarPorId(idUsuario)
                .orElseThrow(() -> new RegraNegocioException("Nenhum usuário com o número " + idUsuario + "."));
        carregarDependencias(usuario);
        return usuario;
    }

    public List<Usuario> listar() {
        return usuarioDao.listarTodos();
    }

    /** Altera nome e e-mail do usuário. */
    public void alterar(Usuario usuario, String novoNome, String novoEmail) {
        String nomeAnterior = usuario.getNome();
        String emailAnterior = usuario.getEmail();
        usuario.setNome(limpar(novoNome));
        usuario.setEmail(novoEmail);
        try {
            usuario.validar();
            usuarioDao.buscarPorEmail(usuario.getEmail())
                    .filter(outro -> outro.getId() != usuario.getId())
                    .ifPresent(outro -> {
                        throw new RegraNegocioException("Esse e-mail já pertence a outro usuário.");
                    });
            usuarioDao.atualizar(usuario);
        } catch (RuntimeException e) {
            usuario.setNome(nomeAnterior);
            usuario.setEmail(emailAnterior);
            throw e;
        }
    }

    /** Exclui o usuário; tarefas e configurações saem junto (ON DELETE CASCADE). */
    public void excluir(Usuario usuario) {
        if (!usuarioDao.excluir(usuario.getId())) {
            throw new RegraNegocioException("Usuário não encontrado para exclusão.");
        }
    }

    /** RF07 - altera uma configuração de acessibilidade do usuário. */
    public ConfiguracaoAcessibilidade alterarConfiguracao(Usuario usuario, String tipo, String valor) {
        ConfiguracaoAcessibilidade configuracao = usuario.getConfiguracao(tipo).orElseGet(() -> {
            ConfiguracaoAcessibilidade nova = new ConfiguracaoAcessibilidade(tipo, valor);
            usuario.adicionarConfiguracao(nova);
            return nova;
        });
        configuracao.alterarConfiguracao(valor);
        if (configuracao.isNova()) {
            configuracaoDao.inserir(configuracao);
        } else {
            configuracaoDao.atualizar(configuracao);
        }
        return configuracao;
    }

    private void carregarDependencias(Usuario usuario) {
        usuario.substituirTarefas(tarefaDao.listarPorUsuario(usuario.getId()));
        configuracaoDao.listarPorUsuario(usuario.getId()).forEach(usuario::adicionarConfiguracao);
        // Usuários antigos ou criados direto no banco recebem as configurações que faltarem.
        for (ConfiguracaoAcessibilidade padrao : ConfiguracaoAcessibilidade.padroes()) {
            if (usuario.getConfiguracao(padrao.getTipo()).isEmpty()) {
                usuario.adicionarConfiguracao(padrao);
                configuracaoDao.inserir(padrao);
            }
        }
    }

    private static String limpar(String texto) {
        return texto == null ? null : texto.trim();
    }
}
