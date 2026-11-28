# Regras de negócio

## Autenticação e autorização

1. As contas de acesso são mantidas no realm `ecommerce` do Keycloak. `POST /auth/register` recebe nome completo, e-mail e senha, separa o nome em primeiro nome e sobrenome para o perfil do provedor e atribui o papel `CLIENTE`; a senha nunca é salva no banco do e-commerce. Dados rejeitados pelo Keycloak retornam 400, e-mail duplicado retorna 409 e falhas de comunicação ou de serviço do provedor retornam 503.
2. O login é feito pelo formulário Angular, que envia e-mail e senha a `POST /auth/login`. O backend usa OIDC Direct Access Grant para autenticar no Keycloak e retorna access/refresh tokens somente em cookies HttpOnly, SameSite=Strict; o Angular não chama o Keycloak nem lê tokens. `GET /auth/me` retorna subject, nome, e-mail, usuário e papéis da sessão ou HTTP 401. `POST /auth/refresh` renova os cookies; `GET /auth/logout` revoga a sessão no Keycloak, remove os cookies e redireciona à aplicação.
3. `ADMIN` pode operar todos os endpoints de gerenciamento (clientes, endereços, catálogo, território e pátio). `CLIENTE` pode navegar pelo catálogo público, mas não pode acessar operações administrativas (HTTP 403). Sem sessão, endpoints administrativos respondem HTTP 401. As leituras públicas incluem carros e suas imagens, marcas, categorias, cores e status de uso.
4. O realm/client são importados e configurados pelos scripts PowerShell do backend, sem console gráfico. `configure-keycloak.ps1` cria o primeiro usuário `ADMIN`, associa o escopo padrão `kc-admin-api` e concede `manage-users` à conta de serviço para habilitar o cadastro pela aplicação via Admin REST API do Keycloak 26.8.2 ou superior. O segredo do client é gerado localmente e nunca deve ser versionado ou enviado ao Angular.
5. O Direct Access Grant recebe a senha no backend e é menos recomendado para aplicações de produção que Authorization Code com PKCE; mantenha HTTPS em produção e não registre credenciais em logs. Cookies de sessão não são persistidos no banco do e-commerce.

## Catálogo de veículos

1. Cadastre marca, categoria e cor antes do carro. Cada carro pertence a exatamente uma de cada. A cor recebe um nome e um `tonalidadeId` (1 = claro, 2 = escuro, 3 = puro). O status de uso descreve a condição do carro (1 = seminovo, 2 = usado). A API expõe esses status em `/status-usos` e `/status-usos/{id}`.
2. Em `/carros`, envie `nome`, `StatusUsoId`, `corId`, `marcaId` e `categoriaId`. A grafia de `StatusUsoId` segue o record de entrada. A resposta expõe `nomeCarro` isolado e `nome` composto por marca e nome do carro. No POST e no PUT, status de uso ausente ou desconhecido e IDs de cor, marca ou categoria ausentes ou inexistentes provocam HTTP 400. GET e PUT por ID retornam 404 quando o carro não existe. Em registros antigos sem status, a consulta devolve statusUso e statusUsoId nulos.
3. É possível consultar carros por nome, status de uso, cor, marca ou categoria. `/marcas`, `/categorias` e `/cores` permitem busca por nome, e cores também por tonalidade. Nessas listagens e nas de carros, a primeira página é `page=0` e o tamanho padrão é `pageSize=5`.
4. Para excluir um item de catálogo em uso, informe um substituto do mesmo tipo. Sem substituto, a exclusão retorna 409. Um ID substituto inexistente ou igual ao original retorna 400. O serviço transfere todos os carros antes da exclusão, na mesma transação. Os parâmetros de consulta são `replacementMarcaId`, `replacementCategoriaId` e `replacementColorId`, respectivamente para marca, categoria e cor.
5. Para excluir um carro, primeiro libere sua vaga. Enquanto houver vínculo, o serviço retorna 409. A exclusão do carro também remove sua imagem.

### Imagem do carro

Cada carro admite uma imagem. Faça `POST /carros/{id}/imagem` em multipart, com o arquivo no campo `imagem`. O serviço aceita de 1 byte a 5 MiB e verifica se a assinatura inicial corresponde ao MIME declarado de JPEG, PNG, GIF ou WebP. O serviço serializa envios e remoções de imagem do mesmo carro. Um novo envio substitui o conteúdo e ativa `possuiImagem`. Os bytes ficam em armazenamento de objetos compatível com S3; no desenvolvimento local, o serviço é SeaweedFS, executado no Windows sem Docker. A tabela `carro_imagem` guarda a chave do objeto e o tipo MIME. A resposta do carro passa a incluir `imagemUrl`. O GET da mesma rota devolve os bytes com seu tipo de mídia ou 404 se faltar o carro ou a imagem. `GET /carros/{id}/imagem/download` baixa o arquivo com nome apropriado. O DELETE remove o objeto e seus metadados e desativa o indicador. Excluir o carro também exclui a imagem do armazenamento.

## Pátio e ocupação

- Crie um setor em `/setores` com nome obrigatório de até 80 caracteres e de 1 a 50 linhas e colunas em cada dimensão. O serviço remove espaços nas pontas e exige nome único sem distinguir maiúsculas de minúsculas. As vagas são cadastradas individualmente.
- Em `/vagas`, informe setor existente, código obrigatório de até 40 caracteres, linha, coluna e tipo (`NORMAL`, `COBERTA` ou `PREFERENCIAL`). As coordenadas começam em 1 e devem caber na grade. O código é único em todo o pátio após remoção dos espaços nas pontas e comparação sem distinção de caixa. A posição é única dentro do setor.
- Uma vaga comporta no máximo um carro e cada carro ocupa no máximo uma vaga. Na criação ou atualização da vaga, um `carroId` existente ocupa a posição. No PUT, envie os demais campos obrigatórios e `carroId: null` para liberá-la. Para transferir o carro, libere a origem antes de associá-lo ao destino. Código, posição ou carro já utilizados em outra vaga geram 409. Setor ou carro informado inexistente e posição fora da grade geram 400.
- Reduzir um setor só é permitido se todas as vagas ainda couberem. Excluir um setor exige remover suas vagas, mesmo as livres. Ambas as violações geram 409. Excluir uma vaga ocupada remove o vínculo e preserva o carro.

## Clientes e endereços

| Cadastro | Campos e validações |
| --- | --- |
| Estado | Nome obrigatório de até 100 caracteres e sigla de duas letras de A a Z. A sigla é armazenada em maiúsculas e deve ser única. |
| Município | Nome obrigatório de até 100 caracteres e estado existente. O nome, aparado e comparado sem distinção de caixa, é único dentro do estado. |
| Cliente | Nome obrigatório de até 120 caracteres, CPF de 11 dígitos sem máscara, e-mail válido de até 160 caracteres e telefone de 10 ou 11 dígitos. O serviço verifica os dois dígitos do CPF, rejeita sequências de um único dígito e impede CPF duplicado (409). Remove espaços nas pontas do nome e do e-mail e grava o e-mail em minúsculas. |
| Endereço | CEP de 8 dígitos, logradouro de até 160 caracteres, número de até 20, bairro de até 100 e município existente são obrigatórios. Complemento é opcional, com até 100 caracteres. O número é textual. |

1. Cadastre o estado em `/estados` e o município em `/municipios`. Cada município pertence a um estado. Cada endereço pertence a um município e deriva dele o estado. A exclusão de estado com municípios ou de município com endereços retorna 409.
2. Crie ou atualize o cliente em `/clientes` com pelo menos um endereço válido. Nesse fluxo o cliente da requisição é o titular dos endereços. O PUT **substitui toda a lista** e recria os endereços enviados. A remoção de órfãos elimina os registros anteriores. A exclusão do cliente também remove seus endereços. A listagem de clientes usa página inicial 0 e tamanho padrão 100.
3. Para operar sobre um endereço individual, use `/enderecos`. Na criação e atualização, informe `clienteId` e `municipioId` existentes. Essa rota também permite excluir um endereço individualmente, inclusive o último. Por isso, a cardinalidade persistente é de zero a muitos endereços por cliente, embora o DTO de cliente exija ao menos um no POST e no PUT.
