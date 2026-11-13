# Checklist de testes da 2ª entrega

A compilação das classes Java foi verificada com `javac --release 17`. A conexão real com o MySQL e os recursos de voz precisam ser testados no computador do grupo, pois dependem da instalação, da porta, da senha, do serviço do banco e do microfone/reconhecedor de fala.

## Preparação

1. Inicie o serviço MySQL Server.
2. No MySQL Workbench, conecte-se à instância correta.
3. Execute `database/script.sql`.
4. Copie `db.properties.example` para `db.properties` e configure porta, usuário e senha.
5. No terminal, na pasta do projeto, execute `mvn clean compile` e depois `mvn exec:java`.

## Testes funcionais

- [ ] Cadastrar um usuário com nome e e-mail.
- [ ] Encerrar e iniciar novamente; confirmar que o usuário continua cadastrado.
- [ ] Entrar com o usuário cadastrado.
- [ ] Cadastrar uma tarefa com título, descrição e data.
- [ ] Consultar/listar as tarefas.
- [ ] Alterar uma tarefa.
- [ ] Marcar uma tarefa como concluída e reabri-la.
- [ ] Excluir uma tarefa.
- [ ] Alterar configurações de acessibilidade e confirmar persistência.
- [ ] No Windows, testar reconhecimento e leitura de voz com microfone e fala pt-BR instalados.

## Evidências recomendadas

Guardar capturas do Workbench mostrando as tabelas e registros, do programa executando o CRUD e, se possível, do teste de voz. Não incluir a senha do banco nas capturas nem enviar `db.properties` ao GitHub.
