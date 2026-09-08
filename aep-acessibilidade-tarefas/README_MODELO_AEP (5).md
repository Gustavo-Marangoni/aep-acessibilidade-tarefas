# Acessibilidade para pessoas com ausência bilateral dos membros superiores

## Sobre o projeto

Aplicação de gerenciamento de tarefas com recursos de acessibilidade voltados para pessoas com ausência bilateral dos membros superiores.

A proposta é permitir o gerenciamento de tarefas de forma simples e oferecer comandos de voz para determinadas funcionalidades da aplicação, buscando reduzir a dependência do teclado e do mouse e contribuir para uma maior autonomia do usuário.

## ODS

**ODS 10 - Redução das Desigualdades**

O projeto está relacionado à ODS 10 por buscar utilizar a tecnologia como uma forma de diminuir barreiras de acesso e promover maior inclusão digital para pessoas com deficiência física.

## Funcionalidades planejadas

* Cadastro de usuário.
* Cadastro de tarefas.
* Consulta de tarefas.
* Alteração de tarefas.
* Conclusão de tarefas.
* Exclusão de tarefas.
* Armazenamento das informações em banco de dados MySQL.
* Configurações de acessibilidade vinculadas ao usuário.
* Comandos de voz para determinadas funcionalidades.

## Tecnologias

* Java
* MySQL
* Git
* GitHub

## Arquitetura do sistema

O sistema será organizado utilizando uma estrutura simples em camadas, separando as partes responsáveis pela interação com o usuário, pelas regras do sistema e pelo acesso ao banco de dados.

A interface será responsável por apresentar as informações e receber as ações do usuário. A camada de regras será responsável pelo funcionamento das principais funcionalidades, como cadastrar, consultar, alterar e excluir tarefas. A camada de acesso aos dados será responsável pela comunicação da aplicação com o banco de dados.

## Estrutura do repositório

```text
/src
/docs
/database
README.md
```

* `/src` - Código-fonte da aplicação.
* `/docs` - Documentação e diagramas do projeto.
* `/database` - Arquivos relacionados ao banco de dados.
* `README.md` - Documentação principal do projeto.

## Requisitos funcionais

### RF01 - Cadastro de usuário

O sistema deverá permitir o cadastro de usuários, armazenando as informações necessárias para utilização da aplicação.

### RF02 - Cadastro de tarefas

O sistema deverá permitir que o usuário cadastre novas tarefas, informando título, descrição e, quando necessário, uma data para realização.

### RF03 - Consulta de tarefas

O sistema deverá permitir que o usuário consulte as tarefas que foram cadastradas.

### RF04 - Alteração de tarefas

O sistema deverá permitir que o usuário altere as informações de uma tarefa já cadastrada.

### RF05 - Exclusão de tarefas

O sistema deverá permitir que o usuário exclua uma tarefa cadastrada.

### RF06 - Conclusão de tarefas

O sistema deverá permitir que uma tarefa seja marcada como concluída, alterando seu status de "Pendente" para "Concluída".

### RF07 - Configurações de acessibilidade

O sistema deverá permitir que sejam armazenadas configurações de acessibilidade relacionadas ao perfil do usuário.

### RF08 - Comandos de voz

O sistema deverá permitir que determinadas funcionalidades da aplicação sejam realizadas por comandos de voz, oferecendo uma alternativa ao uso exclusivo de teclado e mouse.

## Requisitos não funcionais

### RNF01 - Usabilidade

A aplicação deverá possuir uma interface simples e organizada, buscando facilitar a identificação e utilização de suas principais funcionalidades.

### RNF02 - Persistência dos dados

Os dados de usuários, tarefas e configurações deverão ser armazenados em um banco de dados.

### RNF03 - Organização do código

O sistema deverá ser desenvolvido utilizando conceitos de Programação Orientada a Objetos, mantendo as responsabilidades das classes organizadas.

### RNF04 - Acessibilidade

As principais funcionalidades relacionadas ao gerenciamento das tarefas deverão considerar formas de interação que reduzam a dependência exclusiva do teclado e do mouse.

## Regras de negócio

* **RN01** - Cada usuário poderá possuir tarefas cadastradas no sistema.
* **RN02** - Toda tarefa deverá possuir pelo menos um título e um status.
* **RN03** - As tarefas poderão possuir os status "Pendente" ou "Concluída".
* **RN04** - O usuário poderá cadastrar, consultar, alterar e excluir suas tarefas.
* **RN05** - O sistema deverá permitir que determinadas funcionalidades sejam realizadas por comandos de voz como forma alternativa de interação.
* **RN06** - As configurações de acessibilidade deverão estar relacionadas ao perfil do usuário.
* **RN07** - As informações cadastradas deverão ser armazenadas no banco de dados do sistema.

## Modelagem do sistema

A modelagem do sistema será representada por meio do Diagrama de Classes e do Diagrama Entidade-Relacionamento (DER).

Os diagramas estarão disponíveis na pasta `/docs`.

### Diagrama de Classes

O Diagrama de Classes representa as principais classes da aplicação, seus atributos, métodos e relacionamentos.

Entre as principais classes estão:

* Usuário
* Tarefa
* Configuração de Acessibilidade
* Comando
* Comando Cadastrar Tarefa
* Comando Consultar Tarefa

A classe abstrata `Comando` será especializada pelas classes relacionadas aos comandos, permitindo aplicar conceitos de herança e polimorfismo por meio da sobrescrita do método `executar()`.

### Diagrama Entidade-Relacionamento (DER)

O DER representa a estrutura do banco de dados da aplicação.

Inicialmente, serão utilizadas as entidades:

* Usuário
* Tarefa
* Configuração de Acessibilidade

Um usuário poderá possuir várias tarefas cadastradas e configurações relacionadas à acessibilidade.

## Cronograma

> As datas abaixo devem ser preenchidas de acordo com o calendário e os prazos definidos para a turma, mantendo o mesmo cronograma apresentado no PDF da primeira entrega.

| Data      | Atividade                                       | Responsável |
| --------- | ----------------------------------------------- | ----------- |
| PREENCHER | Revisão da descoberta, requisitos e solução     | Lucas       |
| PREENCHER | Revisão da modelagem e dos diagramas            | Gustavo     |
| PREENCHER | Criação e organização do repositório GitHub     | Nicolas     |
| PREENCHER | Implementação das classes e conceitos de POO    | Todos       |
| PREENCHER | Implementação do banco de dados e CRUD          | Todos       |
| PREENCHER | Implementação dos comandos de voz planejados    | Todos       |
| PREENCHER | Testes, correções e preparação da entrega final | Todos       |

## Integrantes

* Lucas Marinho
* Gustavo
* Nicolas

## Como executar

> Preencher na segunda entrega com os passos necessários para instalar as dependências, configurar o MySQL e executar a aplicação.

## Repositório

O projeto será armazenado em um repositório público no GitHub, contendo o código-fonte, a documentação, os diagramas e os arquivos relacionados ao banco de dados.

**Link do repositório:** PREENCHER APÓS A CRIAÇÃO DO REPOSITÓRIO PÚBLICO
