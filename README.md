# Trackerzon
Aplicação de estudo para rastreamento assíncrono de preços usando uma API Spring Boot, RabbitMQ, MariaDB e um worker Python com Playwright.

**Disclaimer**: o projeto tem foco educacional. Os métodos usados para a coleta de dados vão contra o TOS da Amazon e podem banir IPs residenciais ou proxies.

## Componentes
- **Spring Boot API**
    - Fluxo de usuários
    - Gerencia usuários, produtos e relação de produtos rastreados
    - Publica mensagens duráveis no RabbitMQ com `{ productId, url }`
- **RabbitMQ**
    - Desacopla os serviços
    - Mantém a fila `track_product` e a fila de falhas `track_product.failed`
- **Python Worker**
    - Coleta dados de produtos
    - Consome uma mensagem por vez com ACK manual
    - Atualiza o título do produto e grava histórico de preços
    - Pode usar proxies quando `USE_PROXIES=true`

## Como funciona
```
Client <-> Spring API -> RabbitMQ -> Playwright Worker -> Database
```
O cliente acessa diretamente a API Spring. Ao cadastrar uma URL, a API cria ou reaproveita o produto, associa o produto ao usuário autenticado e envia uma mensagem para a fila `track_product`. O worker consome a mensagem, acessa a página do produto, atualiza o título e insere um novo registro em `product_records`.

## Execução local com Docker

1. Crie um `.env` a partir de `.env.example`.
2. Preencha `MYSQL_ROOT_PASSWORD`, `JWT_SECRET_KEY`, `RABBITMQ_DEFAULT_USER` e `RABBITMQ_DEFAULT_PASS`.
3. Execute `docker compose up --build`.

O MariaDB é inicializado com `script.sql`. Se já existir um volume antigo com schema incompatível, remova o volume antes de subir novamente.

### Proxies service
<hr>
O worker pode iniciar o navegador com proxy. Para isso, defina `USE_PROXIES=true` e mantenha o arquivo configurado por `PROXY_FILE` no formato `ip:port:username:password`, uma proxy por linha.

### Playwright service
<hr>
Com essa instância, o navegador acessa a URL do produto, espera o conteúdo carregar e busca título/preço. O worker não cria usuários nem relações de usuário-produto; essa responsabilidade fica na API.

## Limitações e vulnerabilidades conhecidas
A Amazon tem vários sistemas designados a detectar comportamento robótico, fazendo com que seja um dos sites mais difíceis para praticar scraping.

Aqui estão algumas limitações detectadas em desevnolvimento:
- Os proxies que selecionei podem ser facilmente detectáveis, tanto por serem poucos usados em rotação, quanto pela origem deles;
- Scraping em grande escala ainda não é viável por arquitetura;
- Não contorna CAPTCHA.

## Meta do projeto

Este projeto foi desenvolvido como:
- prova de conceito de arquitetura distribuída;
- demonstração de uso de filas e workers;
- projeto de portfólio.

## Testes

- API: `cd api && ./mvnw test`
- Worker: `cd worker && python -m py_compile main.py services/**/*.py`
