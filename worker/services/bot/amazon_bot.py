from bs4 import BeautifulSoup
from datetime import datetime
from decimal import Decimal
from typing import Any
import random
import re


class ScrapingError(Exception):
    pass


class AmazonBot:
    def __init__(self, browser):
        self.browser = browser
        self.title_identifier: str = 'span#productTitle'
        self.price_identifiers: tuple[str, ...] = (
            'span.a-price span.a-offscreen',
            'span#priceblock_ourprice',
            'span#priceblock_dealprice',
            'span.a-price-whole'
        )


    def _open_context(self):
        return self.browser.new_context(
            viewport={'width': 1280, 'height': 800},
            user_agent='Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',
            locale='pt-BR',
            timezone_id='America/Sao_Paulo',
            permissions=['geolocation'],
            java_script_enabled=True
        )


    def access_page(self, info: dict[str, Any]) -> dict[str, Any]:
        context = self._open_context()
        page = None

        try:
            page = context.new_page()
            page.goto(info['url'], timeout=60000, wait_until='networkidle')

            page.wait_for_timeout(random.randint(2000, 4000))
            page.mouse.wheel(0, random.randint(300, 800))
            page.wait_for_timeout(random.randint(1500, 3000))

            content = page.content()

            return self.scrape_value(info, content)
        finally:
            if page:
                page.close()
            context.close()


    def scrape_value(self, info: dict[str, Any], content: str) -> dict[str, Any]:
        html = BeautifulSoup(content, 'html.parser')

        title = html.select_one(self.title_identifier)
        price_text = self.find_price_text(html)

        if not title:
            raise ScrapingError('Product title not found')

        if not price_text:
            raise ScrapingError('Product price not found')

        return {
            'productId': info['productId'],
            'title': title.text.strip(),
            'price': self.parse_price(price_text),
            'url': info['url'],
            'tracked_at': datetime.now()
        }


    def find_price_text(self, html: BeautifulSoup) -> str | None:
        for selector in self.price_identifiers:
            price = html.select_one(selector)
            if price and price.text.strip():
                return price.text.strip()

        whole = html.select_one('span.a-price-whole')
        fraction = html.select_one('span.a-price-fraction')
        if whole and fraction:
            return f'{whole.text},{fraction.text}'

        return None


    def parse_price(self, text: str) -> Decimal:
        cleaned = re.sub(r'[^\d,.]', '', text)
        if not cleaned:
            raise ScrapingError(f'Invalid price: {text}')

        if ',' in cleaned:
            cleaned = cleaned.replace('.', '').replace(',', '.')
        elif cleaned.count('.') > 1 or len(cleaned.rsplit('.', 1)[-1]) == 3:
            cleaned = cleaned.replace('.', '')

        return Decimal(cleaned).quantize(Decimal('0.01'))
