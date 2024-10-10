
# Payment Routing Application

## Índice
1. [Introdução](#introdução)
2. [Requisitos](#requisitos)
3. [Configuração Inicial](#configuração-inicial)
5. [Estrutura do Projeto](#estrutura-do-projeto)
6. [Modelagem de Tabelas](#modelagem-de-tabelas)
7. [Tratamento de Erros](#tratamento-de-erros)
8. [Testes](#testes)
9. [Licença](#licença)

## Introdução

O **Payment Routing Application** é uma aplicação responsável por gerenciar o envio de pagamentos para diferentes filas SQS, dependendo do status de pagamento, com suporte a retry automático em caso de falhas. A aplicação também conta com uma modelagem de dados estruturada para clientes e pagamentos.

A funcionalidade de retry é implementada utilizando o **Spring Retry**, o que garante que tentativas adicionais sejam feitas automaticamente ao falhar no envio de mensagens para as filas SQS.

## Requisitos

Para rodar esta aplicação, você precisará ter instalado:

- [Java 17](https://openjdk.java.net/projects/jdk/17/)
- [Maven](https://maven.apache.org/)
- [PostgreSQL](https://www.postgresql.org/)
- [LocalStack](https://github.com/localstack/localstack)
- [Docker](https://www.docker.com/)

## Configuração Inicial

A aplicação está configurada para usar o **PostgreSQL** em produção e **H2** em memória para os testes.
Para rodar em ambiente local precisa ser no perfil correto. Atravez da variável: `spring_profiles_active=local`.
O projeto já inclui um arquivo `./docker/docker-compose.yml` para subir os serviços necessários, como PostgreSQL e LocalStack.

### Configurar o LocalStack

Para simular as filas SQS localmente, você pode usar o **LocalStack**.

- Crie as filas no LocalStack:

```bash
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name PartialQueue
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name TotalQueue
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name ExcessQueue
```

## Estrutura do Projeto

A estrutura segue a abordagem de **arquitetura limpa**, separando claramente as responsabilidades de cada camada.

- **core**: Contém a lógica de negócio e as entidades principais da aplicação.
- **adapters**: Interface com o mundo externo, incluindo controllers e serviços.
- **application**: DTOs e lógica de validação de dados.
- **usecase**: Casos de uso que envolvem as regras de envio de pagamentos.
- **infrastructure**: Configurações e interações com frameworks e bibliotecas, como o AWS SQS.

### Principais Componentes

- **SendPaymentsSqsUseCaseImpl**: Caso de uso responsável por enviar mensagens para as filas SQS com base no status do pagamento.
- **PaymentController**: Controlador REST que processa requisições de pagamentos.

## Modelagem de Tabelas

### Estrutura das Tabelas

1. **Tabela `clients`**:
   - Representa os clientes da aplicação.
   - A chave primária é `client_id`.
   - Relacionamento 1:N com a tabela `payments`.

```sql
CREATE TABLE clients (
    client_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);
```

2. **Tabela `sellers`**:
   - Representa vendedores, que são uma especialização de clientes.
   - Tomei essa decisão, uma vez que, compartilham dados em comum como informações pessoais e de contato, além de contar com condições especiais, como descontos específicos em suas transações.
   - Usa `client_id` como chave primária, herdando o relacionamento de `clients`.

```sql
CREATE TABLE sellers (
    client_id BIGINT PRIMARY KEY,
    date_contract DATE,
    CONSTRAINT fk_seller_client FOREIGN KEY (client_id) REFERENCES clients(client_id)
);
```

3. **Tabela `payments`**:
   - Armazena informações de pagamento.
   - Cada pagamento pertence a um cliente (relacionamento N:1 com `clients`).

```sql
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    CONSTRAINT fk_payment_client FOREIGN KEY (client_id) REFERENCES clients(client_id)
);
```

4. **Tabela `payment_items`**:
   - Cada pagamento pode ter vários itens.
   - Relacionamento 1:N com a tabela `payments`.

```sql
CREATE TABLE payment_items (
    payment_item_id BIGSERIAL PRIMARY KEY,
    amount NUMERIC(10, 2) NOT NULL,
    payment_status VARCHAR(255),
    payment_id BIGINT NOT NULL,
    CONSTRAINT fk_payment_item_payment FOREIGN KEY (payment_id) REFERENCES payments(id)
);
```

### Relações Entre Tabelas

- **Client** e **Seller**: Um vendedor (`Seller`) é um cliente especial e utiliza o `client_id` como chave primária.
- **Client** e **Payment**: Um cliente pode ter múltiplos pagamentos.
- **Payment** e **PaymentItem**: Um pagamento pode ter múltiplos itens de pagamento.

### Scripts SQL

Os scripts SQL para criar as tabelas estão incluídos no projeto e são carregados automaticamente na inicialização. 
As tabelas são carregadas nos testes quando utilizado o banco de dados H2 e no PostgreSQL elas são carregadas 
junto com o Docker.

## Tratamento de Erros

O tratamento de erros na aplicação segue um padrão JSON para as respostas de erro. Exemplo:

```json
{
    "error": "VALIDATION_ERROR",
    "message": "Validation failed for some fields.",
    "details": {
        "clientId": "Client ID cannot be null"
    }
}
```

- **GENERIC_ERROR**: Erros genéricos, como falhas em recursos inexistentes.
- **SELLER_NOT_FOUND**: Quando um vendedor não é encontrado para o pagamento.
- **PAYMENT_ITEM_NOT_FOUND**: Quando um item de pagamento não é encontrado.

## Testes

A aplicação está coberta por testes unitários e de integração, incluindo:

- **Testes de validação**: Verificam se as regras de negócio são respeitadas.
- **Testes de retry**: Validam o comportamento do Spring Retry ao enviar mensagens para as filas SQS.
- **Testes de falha**: Simulam cenários de erro e verificam as respostas da API.

Para rodar os testes:

```bash
./gradlew test
```

Os relatórios de cobertura de código são gerados usando **JaCoCo** e podem ser visualizados no diretório `.build/jacocoHtml/index.html`.

## Licença

Este projeto está licenciado sob os termos da licença MIT.
