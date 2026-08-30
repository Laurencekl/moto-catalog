# Moto Catalog

**API REST para gestão de anúncios de motos e base de uma vitrine digital.**

Projeto de portfólio desenvolvido para aplicar Java, Spring Boot e PostgreSQL em um problema de negócio: organizar o estoque de motos e facilitar o contato entre comprador e vendedor.

A experiência planejada permite consultar anúncios sem cadastro e conversar com o vendedor pelo WhatsApp. Somente o administrador terá login para gerenciar o estoque. Não há checkout ou processamento de pagamentos no escopo.

> **Em desenvolvimento:** o repositório contém o backend. A interface em React, o contato via WhatsApp e a autenticação administrativa ainda serão implementados. As rotas atuais estão abertas para testes locais.

## Funcionalidades implementadas

- Cadastro, consulta, atualização e exclusão de motos.
- Consulta de detalhes por ID.
- Filtros de categoria, ano e cilindrada para motos disponíveis.
- Controle de disponibilidade com os status `DISPONIVEL` e `VENDIDA`.
- Persistência em PostgreSQL com Spring Data JPA.
- Validação dos dados e tratamento centralizado de erros `400` e `404`.

## Tecnologias

- **Backend:** Java 21, Spring Boot 3.5.15 e Spring Web.
- **Persistência:** PostgreSQL 18, Spring Data JPA e Hibernate.
- **Validação e build:** Jakarta Bean Validation, Maven e Lombok.
- **Base de segurança:** Spring Security e BCrypt; login e autorização em desenvolvimento.
- **Frontend planejado:** React com TypeScript.

## Organização do código

O backend utiliza camadas para separar requisições HTTP, regras de negócio e acesso ao banco.

```text
src/main/java/com/example/motocatalogapi/
├── config/        # Dados iniciais de desenvolvimento
├── controller/    # Endpoints REST
├── exception/     # Exceções e respostas de erro
├── model/         # Entidades e enums
├── repository/    # Consultas e persistência
├── security/      # Configuração inicial de segurança
└── service/       # Regras de negócio
```

## Executar localmente

**Pré-requisitos:** JDK 21, Maven 3.9.x, Git e PostgreSQL em execução.

```powershell
git clone https://github.com/Laurencekl/moto-catalog.git
cd moto-catalog
```

Crie o banco `moto_catalog` e o usuário `motocatalogapi` conforme o [guia de configuração](docs/guia-desenvolvimento.md#2-preparar-o-postgresql). Depois, no mesmo PowerShell:

```powershell
$env:SPRING_DATASOURCE_URL = 'jdbc:postgresql://localhost:5432/moto_catalog'
$env:SPRING_DATASOURCE_USERNAME = 'motocatalogapi'
$senhaBanco = Read-Host 'Senha do banco' -AsSecureString
$env:SPRING_DATASOURCE_PASSWORD = [System.Net.NetworkCredential]::new('', $senhaBanco).Password
$env:SERVER_ADDRESS = '127.0.0.1'

mvn spring-boot:run
```

A API estará disponível em [http://localhost:8080/api/motos](http://localhost:8080/api/motos). Sem anúncios cadastrados, a resposta será `[]`.

Execute os comandos na pasta que contém o `pom.xml`. Ao iniciar pela IDE, configure as mesmas variáveis de ambiente na configuração de execução.

## Endpoints principais

| Método | Rota | Operação |
| --- | --- | --- |
| `GET` | `/api/motos` | Listar todas as motos, inclusive vendidas |
| `GET` | `/api/motos/disponiveis` | Filtrar motos disponíveis |
| `GET` | `/api/motos/{id}` | Consultar detalhes |
| `POST` | `/api/motos` | Cadastrar uma moto |
| `PUT` | `/api/motos/{id}` | Atualizar os dados |
| `PATCH` | `/api/motos/{id}/vendida` | Marcar como vendida |
| `DELETE` | `/api/motos/{id}` | Excluir um anúncio |

Os filtros disponíveis são `categoria`, `anoMinimo`, `anoMaximo`, `cilindradaMinima` e `cilindradaMaxima`. As categorias aceitas são `CUB`, `STREET` e `TRAIL`.

Consulte [exemplos de requisições, validações e respostas HTTP](docs/guia-desenvolvimento.md#endpoints).

## Compilação e testes

```powershell
mvn clean compile
mvn test
```

A suíte atual contém um teste de carregamento do contexto com `@SpringBootTest`, que depende do PostgreSQL e pode inicializar dados e esquema. Use um banco local dedicado. Testes unitários e de endpoints ainda serão adicionados.

## Estado de segurança

Esta versão é destinada ao desenvolvimento local. Todas as rotas usam `permitAll()`, o CORS está aberto e há valores de configuração e administrador de demonstração. Autenticação, autorização e configuração de produção precisam ser concluídas antes de expor a API publicamente.

Veja os [limites atuais e cuidados de execução](docs/guia-desenvolvimento.md#segurança-e-limites-da-versão-atual).

## Próximos passos

- [ ] Implementar login administrativo com JWT e proteger as operações de escrita.
- [ ] Criar catálogo responsivo, página de detalhes e contato via WhatsApp com React e TypeScript.
- [ ] Desenvolver o painel administrativo e o gerenciamento de imagens.
- [ ] Adicionar testes automatizados de endpoints, DTOs, paginação e OpenAPI/Swagger.
- [ ] Separar configurações por ambiente, remover credenciais de exemplo e versionar migrações.
- [ ] Publicar uma demonstração após concluir os requisitos de segurança.

## Autor

Desenvolvido por [Laurence — @Laurencekl](https://github.com/Laurencekl).
