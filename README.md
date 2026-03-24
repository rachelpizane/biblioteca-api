# 📖 Biblioteca API

A Biblioteca API é uma aplicação backend desenvolvida em Java com Spring Boot que simula o gerenciamento de uma biblioteca escolar, permitindo o controle de autores, livros, locatários e o fluxo de aluguéis.

## 🛠️ Tecnologias utilizadas

- Java 21
- Spring Boot
- Maven
- Docker e Docker Compose
- PostgreSQL
- H2 Database
- Flyway
- JUnit
- Mockito
- JaCoCo
- Swagger (OpenAPI)
- MapStruct
- Lombok

## ✨ Principais funcionalidade
- **Cadastro de autores, livros, locatários e aluguéis**: Permite registrar todas as entidades principais da biblioteca com suas respectivas validações de negócio.
- **CRUD completo de locatários**: Permite cadastrar, atualizar, consultar e remover locatários.
- **Listagem de recursos**: Exibe autores e livros cadastrados, com suporte a paginação e filtros.
- **Consulta detalhada**: Permite buscar informações completas de um recurso específico por ID.
- **Gerenciamento de aluguéis**: Realiza o aluguel de um ou mais livros para um locatário, controlando datas e status do processo.
- **Controle de disponibilidade de livros**: Garante que apenas livros disponíveis possam ser alugados.
- **Consulta de livros por contexto**: Permite consultar livros por autor e livros alugados por locatário.
- **Persistência de dados**: Utiliza banco de dados relacional com versionamento via migrations.
- **Documentação interativa da API**: Disponibiliza endpoints documentados para testes e exploração via Swagger.
- **Testes automatizados**: Contempla testes unitários e de integração para garantir a qualidade da aplicação.
- **Execução com Docker**: Permite subir a aplicação e o banco de dados de forma simplificada via containers.

## 🚀 Como rodar o projeto

### Pré-requisitos
Antes de começar, você precisará ter instalado em sua máquina:

- **Docker**
- **Git**
- **Java** (versão 21 - LTS recomendado)
- **Maven** (ou utilizar o wrapper `mvnw` incluído no projeto)

### 1. Clone o repositório
   ```bash
   git clone https://github.com/rachelpizane/biblioteca-api.git
   cd biblioteca-api
   ```

### 2.1. Opção 1: Rodar tudo com Docker
#### 2.1.1 Suba backend + banco de dados
   ```bash
   docker-compose up -d --build
   ```

### 2.2. Opção 2: Rodar backend local + banco no Docker
#### 2.2.1 Suba apenas o banco de dados:
   ```bash
   docker-compose up -d postgres
   ```

#### 2.2.2. Rode a aplicação localmente
   ```bash
   ./mvnw spring-boot:run
   ```

## 🧪 Testes
O projeto possui:
- Testes unitários
- Testes de integração

Para executar os testes automatizados:
   ```bash
   ./mvnw test
   ```

## 📊 Cobertura de código
O projeto utiliza JaCoCo para análise de cobertura.

Para gerar o relatório:
   ```bash
   ./mvnw clean verify
   ```
*OBS: O relatório também é gerado após a execução dos testes automatizados.*

O relatório estará disponível em:
   ```bash
   target/site/jacoco/index.html
   ```
Abra o arquivo index.html no navegador para visualizar os detalhes da cobertura.

## 📄 Documentação da API
A documentação da API está disponível via Swagger:

   ```bash
   http://localhost:8080/swagger-ui/index.html
   ```
Após iniciar a aplicação, acesse o link acima para:

- Visualizar os endpoints disponíveis
- Testar requisições diretamente
- Consultar contratos da API

## 🙋🏻‍♀️ Autora

Desenvolvido por [Rachel Pizane](https://br.linkedin.com/in/rachel-pizane). 💜