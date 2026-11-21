# Contexto do projeto

O TP2-2026-2-Backend é a API de um sistema de gestão de veículos. Ela mantém o catálogo de carros, identifica sua posição no pátio por setor e vaga e cadastra clientes com seus endereços. O modelo atual não vincula clientes a carros. O módulo Maven fica em `topicos2-backend/`.

## Stack e organização

| Componente | Escolha no projeto |
| --- | --- |
| Build | Java 25, Maven Wrapper e plataforma Quarkus 3.39.3, definidos no [pom.xml](../topicos2-backend/pom.xml). |
| API | Quarkus REST com Jakarta REST, Jackson para JSON e SmallRye OpenAPI para a descrição HTTP. |
| Aplicação | CDI (Quarkus Arc) injeta serviços e repositórios. Hibernate Validator valida os DTOs anotados. |
| Persistência | PostgreSQL via JDBC, Hibernate ORM/JPA e repositórios Panache. Imagens ficam em armazenamento de objetos compatível com S3; operações de escrita no banco ficam em serviços transacionais. |
| Testes e distribuição | Quarkus JUnit e REST Assured exercitam a API. O módulo inclui Dockerfiles para JVM e executáveis nativos. |

Em [br/unitins](../topicos2-backend/src/main/java/br/unitins), `resource/` define rotas e converte resultados em DTOs, `dto/` define os contratos de entrada e saída, `service/` implementa os casos de uso, `repository/` concentra o acesso ao banco e `model/` contém entidades, enums e o conversor JPA de status de uso. Todos os registros persistentes herdam a chave numérica de `DefaultEntity`. A imagem fica em tabela própria e é servida por uma rota binária. Assim, as listagens podem informar sua URL sem carregar os bytes.

## Estado atual

| Área | Implementado |
| --- | --- |
| Catálogo | Cadastro, edição, consulta e exclusão de carros, marcas, categorias e cores. Há filtros, paginação, consulta de status de uso e envio, leitura, download e remoção de imagens de carro em armazenamento S3. |
| Pátio | Cadastro de setores com dimensões de grade e de vagas com coordenadas. A API permite associar e liberar carros e verifica conflitos de ocupação. |
| Clientes e território | Cadastro de clientes com validação de CPF e endereços aninhados. Estados, municípios e endereços também têm rotas próprias. |
| Dados de exemplo | [import.sql](../topicos2-backend/src/main/resources/import.sql) popula os três domínios. Na inicialização, o serviço de imagens procura dez modelos pelo nome e associa o PNG ao primeiro carro correspondente que encontrar, se esse carro ainda não tiver imagem. |
| Testes existentes | [Suíte HTTP](../topicos2-backend/src/test/java/br/unitins) com testes de catálogo, clientes, endereços, divisão territorial e pátio. |

## Ambiente local e pendências identificadas no código

A [configuração](../topicos2-backend/src/main/resources/application.properties) aponta para PostgreSQL em `localhost:5432/topicos2db`, desativa Dev Services e permite CORS para as portas locais 4200, 4201 e 4202. Para executar no Windows, entre em `topicos2-backend/` e use `.\mvnw.cmd quarkus:dev` com JDK 25 e uma base de desenvolvimento descartável. A configuração declara `drop-and-create`, que prevê a recriação do esquema. O armazenamento de objetos local executa SeaweedFS diretamente no Windows, pelo script `topicos2-backend/start-storage.ps1`, sem Docker; seus dados ficam em `%LOCALAPPDATA%\SeaweedFS`.

- **Persistência entre execuções.** O repositório não contém migrações versionadas nem configuração de banco exclusiva para testes. Antes de usar dados que precisem ser preservados, definir a estratégia de evolução do esquema e isolar a base de testes.
- **Validação do catálogo.** Os DTOs de carro, marca, cor e categoria não declaram restrições de entrada. O tratamento de IDs inválidos e registros ausentes também varia entre serviços. O serviço de carros valida o status e os vínculos no cadastro e na edição e sinaliza carro ausente na consulta e na edição. A atualização de cor ainda pode terminar silenciosamente quando o ID não existe.
- **Teste herdado.** `GreetingResourceTest` espera uma resposta em `/hello`, mas não há recurso correspondente entre as rotas atuais. Revisar a expectativa desse teste e de sua variante de integração.
- **Concorrência no pátio.** A unicidade da posição de uma vaga é verificada por consulta no serviço. O mapeamento JPA não declara restrição composta para setor, linha e coluna, permitindo que gravações simultâneas contornem a verificação.

- **Inicialização local.** Para desenvolvimento, iniciar o SeaweedFS antes do Quarkus; a API conecta ao endpoint S3 em `localhost:8333` e cria o bucket `car-images` se necessário. O script valida o SHA-256 do executável oficial antes de iniciá-lo.

Os fluxos estão detalhados nas [regras de negócio](REGRAS_NEGOCIO.md). O [modelo de dados](modelo.plantuml) acompanha as entidades JPA; o diagrama legado em `src/main/diagrams/` também representa a referência da imagem no armazenamento S3.
