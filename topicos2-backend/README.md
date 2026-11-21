# topicos2

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

Para o passo a passo de execução do projeto completo (backend, frontend,
PostgreSQL e armazenamento de imagens sem Docker), consulte o
[tutorial](../docs/COMO_EXECUTAR_PROJETO.md).

## Armazenamento de imagens sem Docker

As imagens dos carros são armazenadas em um serviço compatível com a API S3.
Para desenvolvimento local, o projeto usa o SeaweedFS, gratuito e executado
diretamente no Windows, sem Docker, cadastro em nuvem ou forma de pagamento.
O PostgreSQL guarda somente a chave e o tipo MIME; os bytes ficam no bucket
`car-images`.

No PowerShell, na pasta `topicos2-backend`, inicie o armazenamento:

```powershell
.\start-storage.ps1
```

Na primeira execução, o script baixa o SeaweedFS 4.54 do repositório oficial,
valida o SHA-256 e inicia o serviço. O executável e os dados ficam em
`%LOCALAPPDATA%\SeaweedFS`, fora do repositório. Mantenha esse terminal aberto
enquanto usar a aplicação; pressione Ctrl+C para encerrar.

Com o armazenamento ativo, abra outro PowerShell na pasta `topicos2-backend` e
inicie a API:

```powershell
.\mvnw.cmd quarkus:dev
```

O endpoint S3 é <http://localhost:8333> e o bucket é criado automaticamente.
O SeaweedFS usa credenciais locais de desenvolvimento (`admin`/`secret`),
configuradas pelo script. Para usar outro serviço S3, configure
`S3_ENDPOINT`, `S3_REGION`, `S3_BUCKET`, `S3_ACCESS_KEY` e `S3_SECRET_KEY`.
Os testes usam um adaptador em memória e não precisam iniciar o SeaweedFS.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/topicos2-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Related Guides

- REST ([guide](https://quarkus.io/guides/rest)): Build RESTful web services and APIs using Jakarta REST (formerly JAX-RS)
- SmallRye OpenAPI ([guide](https://quarkus.io/guides/openapi-swaggerui)): Generate OpenAPI schemas and serve Swagger UI for REST API documentation
- REST Jackson ([guide](https://quarkus.io/guides/rest#json-serialisation)): Jackson serialization support for Quarkus REST. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it
- Hibernate ORM with Panache ([guide](https://quarkus.io/guides/hibernate-orm-panache)): Simplified JPA/Hibernate data access layer with active record and repository patterns
- JDBC Driver - PostgreSQL ([guide](https://quarkus.io/guides/datasource)): Connect to the PostgreSQL database via JDBC

## Provided Code

### Hibernate ORM

Create your first JPA entity

[Related guide section...](https://quarkus.io/guides/hibernate-orm)


[Related Hibernate with Panache section...](https://quarkus.io/guides/hibernate-orm-panache)


### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
