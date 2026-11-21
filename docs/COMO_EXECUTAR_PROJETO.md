# Como executar o projeto

Este tutorial explica como executar o frontend e o backend no Windows, sem
Docker. Não é necessário criar conta em um serviço de nuvem nem cadastrar forma
de pagamento.

## 1. Pré-requisitos

Instale e deixe disponíveis no `PATH`:

- JDK 25;
- PostgreSQL iniciado;
- Node.js e npm;
- conexão com a internet na primeira execução, para baixar as dependências e o
  SeaweedFS.

Extraia o ZIP mantendo as pastas do backend e do frontend. Os comandos abaixo
devem ser executados dentro das respectivas pastas: `topicos2-backend` (a que
contém `mvnw.cmd`) e `frontend-tp2` (a que contém `package.json`).

## 2. Configure o banco de dados

O backend espera PostgreSQL em `localhost:5432`, com:

- banco: `topicos2db`;
- usuário: `topicos2`;
- senha: `123456`.

Se o banco e o usuário ainda não existirem, crie-os no PostgreSQL como
administrador:

```sql
CREATE ROLE topicos2 WITH LOGIN PASSWORD '123456';
CREATE DATABASE topicos2db OWNER topicos2;
```

Se já estiverem configurados, não é necessário executar esses comandos. A
configuração fica em
[`topicos2-backend/src/main/resources/application.properties`](../topicos2-backend/src/main/resources/application.properties).

> **Atenção:** no ambiente atual, o Hibernate está configurado com
> `drop-and-create`. Ao iniciar o backend, as tabelas são recriadas e os dados
> anteriores do banco são apagados. Use uma base local descartável.

## 3. Inicie o armazenamento de imagens

Abra um PowerShell na pasta `topicos2-backend` e execute:

```powershell
.\start-storage.ps1
```

Na primeira execução, o script baixa o SeaweedFS 4.54 do repositório oficial,
confere o SHA-256 e inicia o serviço S3 local. O executável e os arquivos
armazenados ficam em `%LOCALAPPDATA%\SeaweedFS`, fora do projeto. Não feche esse
terminal enquanto estiver usando o site. O endpoint S3 é
`http://localhost:8333`.

Se o PowerShell informar que a execução de scripts está bloqueada, permita a
execução somente nessa janela e tente novamente:

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
.\start-storage.ps1
```

## 4. Inicie o backend

Abra um segundo PowerShell na mesma pasta `topicos2-backend` e execute:

```powershell
.\mvnw.cmd quarkus:dev
```

Quando iniciar, a API estará disponível em `http://localhost:8080`. Mantenha
esse terminal aberto.

## 5. Inicie o frontend

Abra um terceiro PowerShell na pasta `frontend-tp2` e instale as dependências:

```powershell
npm ci
```

Depois, inicie o Angular:

```powershell
npm start
```

Abra `http://localhost:4200` no navegador.

## 6. Encerrar

Pressione Ctrl+C nos terminais do frontend, backend e SeaweedFS.

## Problemas comuns

- **Conexão recusada na porta 8333:** confira se o SeaweedFS continua rodando no
  primeiro terminal antes de iniciar o backend.
- **Falha de conexão com PostgreSQL:** confira se o serviço está ativo e se o
  banco, usuário, senha e porta correspondem à configuração acima.
- **Porta em uso:** as portas padrão são `5432` (PostgreSQL), `8333`
  (armazenamento), `8080` (backend) e `4200` (frontend). Feche a aplicação que
  estiver usando a porta necessária.
- **Comando `npm` não encontrado:** instale Node.js e abra um novo terminal.
