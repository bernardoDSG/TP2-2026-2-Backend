---
name: quarkus-backend
description: Orienta a implementação e a revisão de funcionalidades backend em Java com Quarkus. Use ao criar ou alterar endpoints, Resources, Services, Repositories, DTOs, validações, contratos HTTP, regras de negócio ou transações, e ao revisar autorização e isolamento entre tenants nesses fluxos.
---

# Backend Java com Quarkus

## 1. Inspecionar antes de editar

Leia o `AGENTS.md` aplicável ao módulo e os documentos de domínio que ele referencia. No TP2, consulte `docs/CONTEXTO_PROJETO.md`, `docs/REGRAS_NEGOCIO.md` e `docs/modelo.plantuml`.

Confira o arquivo de build, os perfis de configuração e os testes do domínio afetado. Siga uma operação semelhante da rota HTTP até a persistência. Identifique o contrato consumido pelos clientes, as validações existentes, o mecanismo de autorização e a fronteira transacional.

Separe o comportamento observado, a regra aprovada e as decisões pendentes. Indique a fonte de cada decisão relevante. Se código, documentação e testes divergirem, descreva a divergência antes de escolher qual comportamento preservar.

## 2. Respeitar o escopo e a aprovação

Nunca invente endpoints, entidades ou regras para preencher lacunas. Use o contrato aprovado ou solicite a decisão específica que falta. Continue as partes independentes que já estiverem autorizadas.

Exija aprovação explícita para criar, alterar ou remover regras de negócio, entidades JPA ou migrations. Isso inclui campos persistentes, relacionamentos, cardinalidades, constraints e scripts de evolução do banco. Um pedido que especifica a mudança já vale como aprovação daquela mudança. Um pedido genérico de funcionalidade não autoriza alterações de modelo por inferência.

Exemplo: se uma nova listagem puder usar uma projeção dos campos existentes, implemente-a com DTO e consulta. Se ela depender de uma nova relação persistente, apresente a relação necessária e seu impacto e aguarde aprovação para essa parte.

## 3. Implementar por domínio e responsabilidade

Preserve a organização de pacotes do projeto e mantenha a mudança concentrada no domínio afetado. Siga o fluxo `Resource → Service → Repository → Entity`.

- **Resource:** receba e valide o DTO de entrada, delegue o caso de uso e devolva o contrato HTTP aprovado. Confira método, rota, parâmetros, formato, códigos de resposta e corpo dos erros.
- **Service:** aplique as regras aprovadas e coordene o caso de uso. Delimite a transação da operação de escrita para que alterações dependentes sejam confirmadas ou revertidas juntas. Considere concorrência e repetição da requisição quando afetarem uma regra.
- **Repository:** concentre consultas e persistência. Aplique os filtros de visibilidade também a buscas por ID, contagens, paginação, atualizações e exclusões.
- **DTO e Entity:** nunca retorne entidades JPA diretamente pela API, inclusive em coleções, campos aninhados ou dentro de `Response`. Mapeie explicitamente a saída para DTOs ou projeções. Em rotas binárias, devolva o conteúdo e os metadados previstos no contrato. Aceite na entrada apenas campos que o cliente pode definir.

Use Bean Validation para formato e obrigatoriedade conforme o contrato. Valide no serviço as regras que dependem de vínculos, estado persistido ou mais de um registro. Preserve a convenção de erros do projeto.

## 4. Verificar autorização e isolamento

Identifique quem pode executar a operação e sobre quais registros. Confira tanto a permissão para a ação quanto o acesso ao objeto específico.

Quando houver multitenancy, obtenha o tenant do contexto autenticado conforme o mecanismo do projeto. Verifique se a operação principal e os IDs relacionados pertencem ao escopo permitido. Inclua listagens, totais e operações de escrita nessa verificação. Um `tenantId` enviado pelo cliente, isoladamente, não comprova acesso.

Se o projeto não definir tenants ou uma política de autorização aplicável, registre o que encontrou. Não introduza papéis, campos ou filtros por suposição. Se a entrega depender dessa política, solicite a decisão antes de expor a operação.

## 5. Testar, revisar e registrar evidências

Confira o perfil de testes e o destino do banco antes de executar a suíte, especialmente quando houver `drop-and-create`. Use o wrapper do projeto e uma base de testes descartável.

Execute os testes do domínio afetado. Cubra o caminho de sucesso e as falhas relevantes à mudança, como entrada inválida, conflito de domínio, acesso negado e rollback após falha parcial. Em sistemas com tenants, verifique que o tenant A não consegue consultar, alterar nem vincular dados do tenant B. Confira também os campos efetivamente serializados na resposta.

Em pedidos de revisão, apresente os achados por impacto, com arquivo, trecho e consequência observável. Em implementações, atualize a documentação afetada conforme o `AGENTS.md`.

Ao concluir, registre o cenário verificado, o comando executado, o resultado observado e as decisões pendentes. Se algum teste não puder rodar, identifique o impedimento e o comportamento que ficou sem confirmação.