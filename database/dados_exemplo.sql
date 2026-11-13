-- Dados de exemplo (opcional). Rode depois do script.sql.
USE aep_tarefas;

INSERT INTO usuario (nome, email) VALUES
    ('Usuário Demonstração', 'demo@aep.com');

SET @id_demo = (SELECT id_usuario FROM usuario WHERE email = 'demo@aep.com');

INSERT INTO configuracao_acessibilidade (id_usuario, tipo, valor) VALUES
    (@id_demo, 'comandos_de_voz', 'nao'),
    (@id_demo, 'leitura_em_voz',  'sim'),
    (@id_demo, 'velocidade_fala', '5');

INSERT INTO tarefa (id_usuario, titulo, descricao, data, status) VALUES
    (@id_demo, 'Consulta médica', 'Levar exames anteriores', '2026-11-20', 'Pendente'),
    (@id_demo, 'Pagar conta de luz', NULL, '2026-11-15', 'Pendente'),
    (@id_demo, 'Ligar para a família', 'Domingo à tarde', NULL, 'Concluída');
