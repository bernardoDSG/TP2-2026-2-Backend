# Como executar o projeto

Este tutorial explica como executar o frontend e o backend no Windows ou no
macOS, sem Docker. Não é necessário criar conta em um serviço de nuvem nem
cadastrar forma de pagamento.

## 1. Pré-requisitos

Instale e deixe disponíveis no `PATH`:

- JDK 25;
- PostgreSQL iniciado;
- Node.js e npm;
- conexão com a internet na primeira execução, para baixar as dependências e o
  SeaweedFS.

Extraia o ZIP mantendo as pastas do backend e do frontend. Os comandos abaixo
devem ser executados dentro das respectivas pastas: `topicos2-backend` (a que
contém `mvnw.cmd` no Windows e `mvnw` no macOS) e `frontend-tp2` (a que contém
`package.json`).

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

## Executar no macOS

Os comandos desta seção são executados no Terminal (zsh). Primeiro, confirme
que o Java, Node.js, npm e PostgreSQL estão disponíveis:

```sh
java -version
node --version
npm --version
```

O Java deve ser a versão 25 e o PostgreSQL precisa estar iniciado e configurado
conforme a seção 2. Execute o SQL de criação do banco com `psql` ou com uma
ferramenta gráfica de administração do PostgreSQL, se ainda for necessário.

### Inicie o armazenamento de imagens

Na primeira execução, baixe e inicie o SeaweedFS oficial. O comando escolhe o
arquivo correto para Apple Silicon (`arm64`, por exemplo, M1/M2/M3/M4) ou Mac
Intel (`x86_64`) e valida seu SHA-256 antes de executá-lo:

```sh
VERSION=4.54
case "$(uname -m)" in
  arm64)
    ASSET=darwin_arm64.tar.gz
    SHA256=cba792274d5881fb3453291c3af53380c513e80e37aea29a2a25d20333eff325
    ;;
  x86_64)
    ASSET=darwin_amd64.tar.gz
    SHA256=c6a5129a2eb4d2c4e441c6e61dec7e1f2b822dcca9fd20dbe271c395a7b47d06
    ;;
  *)
    echo "Arquitetura macOS não suportada: $(uname -m)"
    exit 1
    ;;
esac

SEAWEED_HOME="$HOME/Library/Application Support/SeaweedFS/$VERSION"
SEAWEED_DATA="$HOME/Library/Application Support/SeaweedFS/data"
ARCHIVE="$TMPDIR/$ASSET"
mkdir -p "$SEAWEED_HOME" "$SEAWEED_DATA"

curl -fL "https://github.com/seaweedfs/seaweedfs/releases/download/$VERSION/$ASSET" -o "$ARCHIVE"
echo "$SHA256  $ARCHIVE" | shasum -a 256 -c -
tar -xzf "$ARCHIVE" -C "$SEAWEED_HOME"
chmod +x "$SEAWEED_HOME/weed"
rm "$ARCHIVE"

AWS_ACCESS_KEY_ID=admin AWS_SECRET_ACCESS_KEY=secret \
  "$SEAWEED_HOME/weed" mini \
  "-dir=$SEAWEED_DATA" -s3.port=8333 -bucket=car-images
```

Mantenha esse Terminal aberto enquanto estiver usando o site. O armazenamento
S3 estará disponível em `http://localhost:8333`, e os arquivos persistirão em
`~/Library/Application Support/SeaweedFS/data`.

### Inicie o backend

Abra outro Terminal, entre na pasta `topicos2-backend` extraída do ZIP e
execute:

```sh
chmod +x ./mvnw
./mvnw quarkus:dev
```

O `chmod` é necessário porque o arquivo do Maven Wrapper pode não manter a
permissão de execução ao extrair o ZIP. Quando iniciar, a API estará em
`http://localhost:8080`. Mantenha esse Terminal aberto.

### Inicie o frontend

Abra um terceiro Terminal, entre na pasta `frontend-tp2` extraída do ZIP e
execute:

```sh
npm ci
npm start
```

Abra `http://localhost:4200` no navegador.

## Encerrar

No Windows ou macOS, pressione Ctrl+C nos terminais do frontend, backend e
SeaweedFS.

## Problemas comuns

- **Conexão recusada na porta 8333:** confira se o SeaweedFS continua rodando no
  primeiro terminal antes de iniciar o backend.
- **Falha de conexão com PostgreSQL:** confira se o serviço está ativo e se o
  banco, usuário, senha e porta correspondem à configuração acima.
- **Porta em uso:** as portas padrão são `5432` (PostgreSQL), `8333`
  (armazenamento), `8080` (backend) e `4200` (frontend). Feche a aplicação que
  estiver usando a porta necessária.
- **Comando `npm` não encontrado:** instale Node.js e abra um novo terminal.
