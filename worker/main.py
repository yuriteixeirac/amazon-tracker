from services.bot import AmazonBot
from services.queue import RabbitMQ
from services.database import Database
from services.proxies import get_random_proxy
from playwright.sync_api import sync_playwright
from playwright_stealth import Stealth
import json, time, random

rabbittMQ = RabbitMQ()
database = Database()


def start_scraping(ch, method, properties, body) -> None:
    message = json.loads(body)

    time.sleep(random.uniform(25, 45))

    with Stealth().use_sync(sync_playwright()) as pw:
        browser = pw.chromium.launch(
            proxy=get_random_proxy()
        )

        try:
            amzn = AmazonBot(browser)
            found_data = amzn.access_page(message)
            database.insert_record(found_data)
        finally:
            browser.close()


def main() -> None:
    rabbittMQ.listen('track_product', callback=start_scraping)


if __name__ == '__main__':
    main()
