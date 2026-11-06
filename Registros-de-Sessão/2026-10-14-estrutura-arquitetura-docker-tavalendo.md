---
tipo: sessão
data: 2026-10-14
projeto: "[[tavalendo]]"
participantes: []
status: concluído
tags:
  - tavalendo
  - spring-boot
  - arquitetura
  - docker
---

# 2026-10-14 - Estrutura, Arquitetura e Docker do Tavalendo

## Referência Rápida
- Tópicos: criação da estrutura de pacotes e classes do backend, implementação de classes base genéricas em pt-BR, configuração de segurança Stateless JWT, containerização com Docker e documentação Javadoc.
- Projeto: [[tavalendo]]
- Resultado: estrutura completa de pacotes `com.ursia.tavalendo` implementada em Java 21 com Spring Boot, classes padronizadas em português, `Dockerfile` e `docker-compose.yml` configurados com PostgreSQL 16 e todas as classes documentadas com Javadoc.

## Decisões Tomadas
- Adotar arquitetura em camadas voltada para integração RESTful com aplicativo mobile em Expo (Controllers, Services, Repositories e DTOs com `record` do Java 21).
- Criar base genérica reutilizável em `core/base` (`EntidadeBase`, `ServiceGenerico`, `ServiceGenericoAbstrato`, `ControllerGenerico` e `RespostaApi`) com suporte a auditoria JPA e exclusão lógica (soft delete).
- Implementar autenticação Stateless com JWT (Access Token e Refresh Token) e controle de acesso baseado em papéis (`Role`: `ADMIN`, `USER`) com `@PreAuthorize`.
- Padronizar a nomenclatura de todas as classes do sistema em português (`pt-BR`), substituindo os nomes iniciais em inglês.
- Containerizar a aplicação com `Dockerfile` multi-stage (Maven + Eclipse Temurin 21 JRE Alpine) e `docker-compose.yml` orquestrando a API e o banco PostgreSQL 16.
- Documentar todas as classes, interfaces, enums e records do projeto com Javadoc explicando suas responsabilidades.

## Principais Aprendizados
- O projeto utiliza Java 21 com Spring Boot (`spring-boot-starter-data-jpa`, `spring-boot-starter-security`, `spring-boot-starter-validation`, `spring-boot-starter-webmvc`, `postgresql` e `lombok`).
- O uso de variáveis de ambiente com fallback em `application.properties` permite que a aplicação conecte automaticamente ao banco local (`localhost:5432`) durante o desenvolvimento na IDE e ao container (`db:5432`) quando executada via Docker Compose.

## Tarefas Pendentes
- [ ] Nenhuma tarefa pendente registrada na sessão.

## Registro Bruto
- Solicitação inicial do usuário para criar a estrutura de pacotes do projeto `com.ursia.tavalendo` (`config`, `security/jwt`, `security/custom`, `exception/custom`, `core/base`, `core/utils`, `modules/usuario` e `modules/produto`).
- Envio das diretrizes arquiteturais pelo usuário: integração com app mobile Expo, uso de `record` para DTOs, classes genéricas base, tratamento global de erros, autenticação Stateless JWT com Refresh Token e RBAC, configuração de CORS e deploy com Docker/PostgreSQL.
- Confirmação do usuário para gerar toda a estrutura já com a base arquitetural implementada.
- Criação inicial dos pacotes e classes base, configurações, segurança JWT, tratamento de exceções e módulos `usuario` e `produto`.
- Solicitação do usuário para alterar os nomes das classes para português (`pt-BR`), como `ServiceGenerico` e `ControllerGenerico`.
- Refatoração completa das classes para português (`EntidadeBase`, `ServiceGenerico`, `ServiceGenericoAbstrato`, `ControllerGenerico`, `RespostaApi`, `DataUtils`, `ConfiguracaoCors`, `ConfiguracaoJpa`, `ConfiguracaoSwagger`, `ConfiguracaoSeguranca`, `ProvedorTokenJwt`, `FiltroAutenticacaoJwt`, `UsuarioDetalhesService`, `ConfiguracaoCodificadorSenha`) e remoção dos arquivos anteriores em inglês.
- Solicitação do usuário para configurar o projeto em Docker.
- Criação de `Dockerfile` multi-stage, `docker-compose.yml` (serviços `db` PostgreSQL 16 e `api`), `.dockerignore` e atualização de `src/main/resources/application.properties`.
- Solicitação do usuário para adicionar docstring (Javadoc) em cada classe explicando o que ela faz.
- Adição de comentários Javadoc em todas as 27 classes, interfaces, enums e records do projeto.
- Execução do comando `/compress` para registro da sessão.
