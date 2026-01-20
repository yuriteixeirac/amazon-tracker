from bs4 import BeautifulSoup
from datetime import datetime
import random


class AmazonBot:
    def __init__(self, browser):
        self.browser = browser
        self.title_identifier: str = 'span#productTitle'
        self.price_identifier: str = 'span.a-price-whole'


    def _open_context(self):
        return self.browser.new_context(
            viewport={'width': 1280, 'height': 800},
            user_agent='Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',
            locale='pt-BR',
            timezone_id='America/Sao_Paulo',
            permissions=['geolocation'],
            java_script_enabled=True
        )


    def access_page(self, info: dict[str]) -> str:
        context = self._open_context()

        page = context.new_page()
        page.goto(info['url'], timeout=60000, wait_until='networkidle')

        page.wait_for_timeout(random.randint(2000, 4000))
        page.mouse.wheel(0, random.randint(300, 800))
        page.wait_for_timeout(random.randint(1500, 3000))

        content = page.content()

        element = self.scrape_value(info['url'], content, user_id=info['userId'])
        if element is None:
            page.screenshot(path='ss.png')
        
        page.close()
        context.close()

        return element


    def scrape_value(self, url: str, content: str, user_id: int = None) -> dict[str]:
        html = BeautifulSoup(content, 'html.parser')

        title = html.select_one(self.title_identifier)
        price = html.select_one(self.price_identifier)

        if not title or not price:
            return None

        result = {
            'title': None or title.text.strip(),
            'price': price.text[:-1].strip(),
            'url': url,
            'tracked_at': datetime.now()
        }

        if user_id:
            result['userId'] = user_id

        return result
