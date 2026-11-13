package br.com.aep.tarefas.comando;

import br.com.aep.tarefas.interacao.TipoResposta;
import br.com.aep.tarefas.model.Usuario;
import br.com.aep.tarefas.util.Texto;

import java.util.List;

/** RF01 - consulta, alteração e exclusão do próprio usuário. */
public class ComandoMinhaConta extends Comando {

    public ComandoMinhaConta() {
        super(8, "Minha conta (alterar ou excluir)", "minha conta", "meus dados", "perfil");
    }

    @Override
    public void executar(ContextoAplicacao contexto) {
        Usuario usuario = contexto.getUsuario();
        contexto.getSaida().lista("Minha conta", List.of(
                "Nome: " + usuario.getNome(),
                "E-mail: " + usuario.getEmail(),
                "1 - Alterar nome e e-mail",
                "2 - Excluir minha conta",
                "0 - Voltar"));
        int escolha = contexto.perguntarNumero("Escolha uma opção.");
        if (escolha == 1) {
            alterar(contexto, usuario);
        } else if (escolha == 2) {
            excluir(contexto, usuario);
        }
    }

    private void alterar(ContextoAplicacao contexto, Usuario usuario) {
        String nome = contexto.perguntar("Novo nome (vazio ou manter para não mudar):", TipoResposta.TEXTO_LIVRE);
        String email = contexto.perguntar("Novo e-mail (vazio ou manter para não mudar):", TipoResposta.TEXTO_LIVRE);
        contexto.getUsuarioService().alterar(usuario,
                Texto.ehManter(nome) ? usuario.getNome() : nome,
                Texto.ehManter(email) ? usuario.getEmail() : email);
        contexto.getSaida().mensagem("Dados atualizados. " + usuario.resumo());
    }

    private void excluir(ContextoAplicacao contexto, Usuario usuario) {
        if (!contexto.perguntarSimNao("Isso apaga sua conta e todas as suas tarefas. Confirmar?")) {
            contexto.getSaida().mensagem("Conta mantida.");
            return;
        }
        contexto.getUsuarioService().excluir(usuario);
        contexto.getSaida().mensagem("Conta excluída. Até logo, " + usuario.getNome() + ".");
        contexto.encerrarSessao();
    }
}
