# Guia de desenvolvimento

Instruções de configuração, exemplos da API e testes locais do Moto Catalog.

[Voltar à apresentação do projeto](../README.md)

## Como executar

### Pré-requisitos

- JDK 21 configurado no terminal e na IDE.
- Maven 3.9.x disponível pelo comando `mvn`.
- PostgreSQL instalado e em execução; o ambiente local usa a versão 18.
- Git para clonar o repositório.

Confira as ferramentas:

```powershell
java -version
mvn -version
```

### 1. Clonar o repositório

```powershell
git clone https://github.com/Laurencekl/moto-catalog.git
cd moto-catalog
```

Execute os comandos Maven na pasta que contém o `pom.xml`. Em uma organização local com pastas separadas para backend e frontend, entre primeiro na pasta da API.

### 2. Preparar o PostgreSQL

Conecte-se com um administrador do PostgreSQL:

```powershell
psql -h localhost -U postgres -d postgres
```

Se `psql` não estiver no `PATH`, uma instalação padrão do PostgreSQL 18 no Windows pode ser acessada assim:

```powershell
& "C:\Program Files\PostgreSQL\18\bin\psql.exe" -h localhost -U postgres -d postgres
```

No terminal do PostgreSQL, execute os comandos abaixo separadamente. Eles pressupõem que o usuário e o banco ainda não existem:

```sql
CREATE ROLE motocatalogapi LOGIN;
```

Defina uma senha local para esse usuário. O comando solicitará a senha duas vezes, sem escrevê-la no histórico SQL:

```text
\password motocatalogapi
```

Crie o banco:

```sql
CREATE DATABASE moto_catalog OWNER motocatalogapi;
```

Saia do PostgreSQL:

```text
\q
```

O usuário `motocatalogapi` é uma credencial técnica da aplicação. Ele não representa um visitante do catálogo e não deve ser um superusuário do banco.

### 3. Configurar a aplicação

O arquivo `src/main/resources/application.properties` contém valores de desenvolvimento. Você pode sobrescrevê-los com variáveis de ambiente, sem editar ou versionar suas credenciais.

No PowerShell que será usado para iniciar a API:

```powershell
$env:SPRING_DATASOURCE_URL = 'jdbc:postgresql://localhost:5432/moto_catalog'
$env:SPRING_DATASOURCE_USERNAME = 'motocatalogapi'

# Digite a mesma senha definida para o usuário motocatalogapi.
$senhaBanco = Read-Host 'Senha do banco' -AsSecureString
$env:SPRING_DATASOURCE_PASSWORD = [System.Net.NetworkCredential]::new('', $senhaBanco).Password

# Mantém a API acessível apenas pelo próprio computador durante os testes.
$env:SERVER_ADDRESS = '127.0.0.1'
```

Essas variáveis valem para a sessão atual do PowerShell e para os processos iniciados por ela. Se executar pela IDE, configure as mesmas variáveis na configuração de execução.

| Configuração | Valor padrão do projeto |
| --- | --- |
| Nome da aplicação | `moto-catalog-api` |
| Porta HTTP | `8080` |
| Banco | `moto_catalog` |
| Usuário do banco | `motocatalogapi` |
| Perfil ativo | `dev` |
| Atualização do esquema | `spring.jpa.hibernate.ddl-auto=update` |

No perfil `dev`, a aplicação cria um administrador de demonstração caso ele ainda não exista. Esse registro não significa que o login já esteja implementado. Os valores de desenvolvimento não devem ser usados em produção.

### 4. Iniciar a API

```powershell
mvn spring-boot:run
```

Mantenha o terminal aberto enquanto utiliza a aplicação. Para encerrá-la, pressione `Ctrl + C`.

Acesse a listagem em [http://localhost:8080/api/motos](http://localhost:8080/api/motos). Um banco sem motos cadastradas retorna `[]`. Não há uma interface web implementada nesta versão.

## Endpoints

Base local: `http://localhost:8080`

**Todos os endpoints abaixo estão atualmente sem autenticação.** A restrição das operações de escrita ao administrador faz parte dos próximos passos.

| Método | Rota | Descrição | Resposta de sucesso atual |
| --- | --- | --- | --- |
| `GET` | `/api/motos` | Lista todas as motos, inclusive vendidas | `200` com lista |
| `GET` | `/api/motos/disponiveis` | Lista motos disponíveis com filtros | `200` com lista |
| `GET` | `/api/motos/{id}` | Busca uma moto pelo ID | `200` com objeto |
| `POST` | `/api/motos` | Cadastra uma moto | `200` com objeto |
| `PUT` | `/api/motos/{id}` | Atualiza os dados de uma moto | `200` com objeto |
| `PATCH` | `/api/motos/{id}/vendida` | Marca uma moto como vendida | `200` com objeto |
| `DELETE` | `/api/motos/{id}` | Exclui uma moto | `200` sem corpo |

Os códigos de sucesso refletem o controller atual. A adoção de `201 Created` no cadastro e `204 No Content` na exclusão está planejada.

### Filtros do catálogo

O endpoint `/api/motos/disponiveis` aceita os seguintes parâmetros opcionais:

| Parâmetro | Tipo | Valores ou comportamento |
| --- | --- | --- |
| `categoria` | Enum | `CUB`, `STREET` ou `TRAIL` |
| `anoMinimo` | Inteiro | Ano maior ou igual ao informado |
| `anoMaximo` | Inteiro | Ano menor ou igual ao informado |
| `cilindradaMinima` | Inteiro | Cilindrada maior ou igual à informada |
| `cilindradaMaxima` | Inteiro | Cilindrada menor ou igual à informada |

Essa consulta retorna somente motos com status `DISPONIVEL`, ordenadas pelo ID de forma decrescente.

```powershell
Invoke-RestMethod -Uri 'http://localhost:8080/api/motos/disponiveis?categoria=STREET&anoMinimo=2020&cilindradaMaxima=200'
```

### Cadastrar uma moto de exemplo

Com a API em execução, abra outro PowerShell. O exemplo abaixo grava um registro no banco; os dados são fictícios:

```powershell
$novaMoto = @{
    nome = 'Honda CG 160 Titan'
    marca = 'Honda'
    modelo = 'CG 160 Titan'
    ano = 2025
    cilindrada = 162
    quilometragem = 0
    cor = 'Vermelha'
    descricao = 'Anuncio de demonstracao para o portfolio'
    imagemUrl = $null
    telefoneVendedor = '5500000000000'
    categoria = 'STREET'
} | ConvertTo-Json

$requisicao = @{
    Uri = 'http://localhost:8080/api/motos'
    Method = 'Post'
    ContentType = 'application/json; charset=utf-8'
    Body = [System.Text.Encoding]::UTF8.GetBytes($novaMoto)
}

Invoke-RestMethod @requisicao
```

O ID é gerado pelo banco e não precisa ser enviado. Quando o status não é informado no cadastro, o service define `DISPONIVEL`. Repetir esse comando cria outro registro.

Para atualizar uma moto com `PUT`, envie todos os campos que deseja manter, incluindo o `status`. A implementação atual substitui os campos; não é uma atualização parcial.

### Dados e validações

- `nome`, `marca`, `modelo`, `cor` e `telefoneVendedor` não podem ser vazios.
- `ano`, `cilindrada`, `quilometragem` e `categoria` são obrigatórios.
- O ano deve estar entre `1900` e `2100`.
- A cilindrada deve ser maior que zero e a quilometragem não pode ser negativa.
- Nome, marca e modelo possuem limites de `120`, `60` e `100` caracteres.
- A descrição é opcional e aceita até `1000` caracteres.
- O telefone deve ter entre `8` e `20` caracteres. Ainda não há validação de formato ou de existência do número.
- `imagemUrl` é opcional; upload de imagens ainda não está implementado.
- As categorias são `CUB`, `STREET` e `TRAIL`. Os status são `DISPONIVEL` e `VENDIDA`.

## Respostas de erro

O `ApiExceptionHandler` centraliza dois casos. Os exemplos abaixo são ilustrativos; a data e os campos retornados variam conforme a requisição.

### Moto não encontrada — HTTP 404

```json
{
  "dataHora": "2026-08-30T18:00:00",
  "status": 404,
  "erro": "Moto não encontrada",
  "mensagem": "Moto nao encontrada com o id: 999999"
}
```

### Falha de validação — HTTP 400

Exemplo de resposta para uma requisição com nome vazio e os demais campos válidos:

```json
{
  "dataHora": "2026-08-30T18:00:00",
  "status": 400,
  "erro": "Dados inválidos",
  "campos": {
    "nome": "O nome da moto é obrigatório"
  }
}
```

Outros erros, como JSON malformado e valores de enum inválidos, ainda não possuem tratamento personalizado nesse handler.

## Testes

Para verificar a compilação:

```powershell
mvn clean compile
```

Para executar os testes existentes:

```powershell
mvn test
```

A suíte atual contém um teste de carregamento do contexto com `@SpringBootTest`. Ele precisa de um PostgreSQL acessível e das variáveis de conexão configuradas. Como carrega a aplicação com suas configurações de desenvolvimento, pode atualizar o esquema e executar o inicializador de dados. Use um banco local dedicado, nunca um banco de produção.

Testes unitários das regras de negócio e testes automatizados dos endpoints ainda serão adicionados. Compilar com sucesso não comprova, por si só, o comportamento HTTP da API.

Para verificar manualmente o erro `404`, consulte um ID inexistente. No Windows, `curl.exe -i` também mostra o status HTTP:

```powershell
curl.exe -i http://localhost:8080/api/motos/999999
```

Para verificar uma requisição inválida, envie um objeto vazio. O esperado é `400`, sem cadastrar uma moto:

```powershell
curl.exe -i -X POST http://localhost:8080/api/motos -H "Content-Type: application/json" -d '{}'
```

## Segurança e limites da versão atual

- A configuração utiliza `permitAll()`: as operações administrativas ainda não estão protegidas.
- O controller aceita qualquer origem no CORS e o CSRF está desabilitado.
- Existem credenciais e uma chave de exemplo na configuração de desenvolvimento. Não reutilize esses valores nem versione credenciais reais.
- O perfil `dev` está ativo por padrão e cria um administrador de demonstração.
- JWT possui dependências declaradas, mas não há autenticação por token implementada.
- O esquema é atualizado automaticamente pelo Hibernate; ainda não há migrações versionadas.
- As listagens não possuem paginação.

Antes de uma publicação, será necessário implementar autenticação e autorização, revisar CORS/CSRF para o modelo escolhido, separar os ambientes, configurar segredos e definir migrações do banco.
