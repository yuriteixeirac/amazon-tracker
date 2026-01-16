# Amazon Scraper
Um crawler simples feito com a ferramenta de testes Playwright. Recebe um link para um produto da Amazon e retorna o seu preço. Surgiu como módulo de um projeto maior abandonado.

## Requisitos
- Python 3.12+

Para instalar as dependências do projeto:
```
pip install requirements.txt
```

## Executando
Para iniciar o programa, um simples
```
python3 main.py
```
deve ser executado.

No primeiro momento, o programa pede a entrada do usuário, esperando o link de um produto presente no site da Amazon.

Ao receber esse link, uma instância do bot é criada e tenta alcançar o preço desse produto, retornando com o valor.