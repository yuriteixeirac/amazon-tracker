# Playwright worker
Aplicação para scraping de dados da Amazon ao receber mensagens através do RabbitMQ, utilizando Playwright, proxies e interações com o banco de dados.

## Dependências
Para a instalação de todas as dependências do bot:

```
# Crie um ambiente virtual com esse comando:
python3 -m venv .venv

# Para ativá-lo (Linux):
source .venv/bin/activate

# Instalando as dependências
pip install -r requirements.txt
```

## Variáveis de ambiente

- `RABBITMQ_HOST`, `RABBITMQ_USER`, `RABBITMQ_PASS`: conexão com RabbitMQ.
- `TRACK_PRODUCT_QUEUE`: fila de entrada, padrão `track_product`.
- `DB_HOST`, `DB_PORT`, `DB_USER`, `DB_PASSWORD`, `DB_NAME`: conexão com MariaDB.
- `USE_PROXIES`: ativa proxies quando `true`.
- `PROXY_FILE`: arquivo com proxies no formato `ip:port:username:password`.
- `SCRAPE_MIN_DELAY_SECONDS` e `SCRAPE_MAX_DELAY_SECONDS`: intervalo aleatório antes de cada scraping.

## Contrato da fila

O worker espera mensagens JSON com este formato:

```json
{
  "productId": 1,
  "url": "https://www.amazon.com.br/..."
}
```

Mensagens inválidas ou falhas de processamento são publicadas em `track_product.failed` com o erro nos headers.
