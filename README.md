# Amazon Tracker
Essa aplicação foi pensado como prova de conceito e estudo para a construção de um sistema desacoplado de rastreamento de preços a partir de uma API back-end e um worker independente que executa um script de web scraping de modo controlado e assíncrono.

**Disclaimer**: o projeto tem foco educacional. Os métodos usados para a coleta de dados vão contra o TOS da Amazon e podem banir IPs residenciais ou proxies.

## Componentes
- **Spring Boot API**
    - Fluxo de usuários
    - Gerencia o domínio da aplicação
    - Publica mensagens ao RabbitMQ
- **RabbitMQ**
    - Desacopla os serviços
    - Escalabilidade horizontal
- **Python Worker**
    - Coleta dados de produtos
    - Consome uma mensagem por vez
    - Aplica proxies
    - Acessa o banco de dados

## Como funciona
```
Client <-> Spring API ->  RabbitMQ -> Playwright Worker -> Database
```
|O cliente acessa diretamente a API Spring, que gerencia o domínio da aplicação, incluindo usuários, produtos e registros de preços. A cada requisição para registro de produto, o `RabbitMQService` envia um JSON para a fila `track_product`, que é interpretado pelo worker.

### Proxies service
<hr>
O worker inicia uma instância de um browser, mas essa depende de um serviço de proxy. O único SaaS que providencia proxies gratuitas que consegui acesso foi o WebShare (free tier com 10 proxies). O package `proxy_service` faz uma requisição para a API do WebShare e registra as proxies em um arquivo `data/proxies.txt`, que é utilizado pela função `get_random_proxy`, que seleciona uma proxy aleatória para tentar burlar o sistema anti-bot da Amazon.

### Playwright service
<hr>
Com essa instância, o browser acessa a URL do produto, espera o conteúdo carregar e busca os elementos referentes ao título e preço do produto, e então, contata o serviço do banco de dados relacional, onde, caso seja o primeiro registro, insere o produto no banco de dados, o registro e a relação entre produtos e usuários.

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

## A fazer
- 

estamos buscando..

