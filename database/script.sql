-- =====================================================================
-- AEP - Acessibilidade para pessoas com ausência bilateral dos membros
-- superiores | Script de criação do banco de dados (MySQL 8+)
--
-- As tabelas seguem o DER da 1ª entrega (docs/diagrama 2 .jpeg) e são
-- compatíveis com as classes Java do pacote br.com.aep.tarefas.model:
--   USUARIO                    -> Usuario
--   TAREFA                     -> Tarefa
--   CONFIGURACAO_ACESSIBILIDADE -> ConfiguracaoAcessibilidade
-- =====================================================================

CREATE DATABASE IF NOT EXISTS aep_tarefas
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE aep_tarefas;

-- ---------------------------------------------------------------------
-- USUARIO (classe Usuario)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuario (
    id_usuario INT          NOT NULL AUTO_INCREMENT,
    nome       VARCHAR(100) NOT NULL,
    email      VARCHAR(150) NOT NULL,
    CONSTRAINT pk_usuario PRIMARY KEY (id_usuario),
    CONSTRAINT uq_usuario_email UNIQUE (email)
);

-- ---------------------------------------------------------------------
-- TAREFA (classe Tarefa) - um usuário possui N tarefas (composição 1:N).
-- ON DELETE CASCADE: ao excluir o usuário, as tarefas dele deixam de existir.
-- RN02: toda tarefa tem título e status. RN03: status Pendente ou Concluída.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tarefa (
    id_tarefa  INT          NOT NULL AUTO_INCREMENT,
    id_usuario INT          NOT NULL,
    titulo     VARCHAR(120) NOT NULL,
    descricao  VARCHAR(500) NULL,
    data       DATE         NULL,
    status     VARCHAR(20)  NOT NULL DEFAULT 'Pendente',
    CONSTRAINT pk_tarefa PRIMARY KEY (id_tarefa),
    CONSTRAINT fk_tarefa_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario) ON DELETE CASCADE,
    CONSTRAINT ck_tarefa_status CHECK (status IN ('Pendente', 'Concluída'))
);

-- ---------------------------------------------------------------------
-- CONFIGURACAO_ACESSIBILIDADE (classe ConfiguracaoAcessibilidade)
-- Um usuário possui N configurações (RN06). Cada tipo aparece uma vez
-- por usuário.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS configuracao_acessibilidade (
    id_configuracao INT         NOT NULL AUTO_INCREMENT,
    id_usuario      INT         NOT NULL,
    tipo            VARCHAR(50) NOT NULL,
    valor           VARCHAR(50) NOT NULL,
    CONSTRAINT pk_configuracao PRIMARY KEY (id_configuracao),
    CONSTRAINT fk_configuracao_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario) ON DELETE CASCADE,
    CONSTRAINT uq_configuracao_usuario_tipo UNIQUE (id_usuario, tipo)
);
