from services.bot import AmazonBot
from services.queue import PermanentMessageError, RabbitMQ
from services.database import Database
from services.proxies import get_random_proxy
from playwright.sync_api import sync_playwright
from playwright_stealth import Stealth
import json, logging, os, time, random


logging.basicConfig(
    level=os.getenv('LOG_LEVEL', 'INFO'),
    format='%(asctime)s %(levelname)s %(name)s %(message)s'
)
logger = logging.getLogger(__name__)

database = None


def start_scraping(body: bytes) -> None:
    message = parse_message(body)

    delay_min = float(os.getenv('SCRAPE_MIN_DELAY_SECONDS', '25'))
    delay_max = float(os.getenv('SCRAPE_MAX_DELAY_SECONDS', '45'))
    time.sleep(random.uniform(delay_min, delay_max))

    proxy = get_random_proxy()
    launch_options = {'headless': True}
    if proxy:
        launch_options['proxy'] = proxy

    with Stealth().use_sync(sync_playwright()) as pw:
        browser = pw.chromium.launch(**launch_options)

        try:
            amzn = AmazonBot(browser)
            found_data = amzn.access_page(message)
            database.insert_record(found_data)
            logger.info('Tracked product %s', message['productId'])
        finally:
            browser.close()


def parse_message(body: bytes) -> dict:
    try:
        message = json.loads(body.decode('utf-8'))
    except json.JSONDecodeError as exception:
        raise PermanentMessageError('Invalid JSON') from exception

    if not isinstance(message, dict):
        raise PermanentMessageError('Message must be a JSON object')

    if not message.get('productId') or not message.get('url'):
        raise PermanentMessageError('Message must contain productId and url')

    return message


def main() -> None:
    global database

    database = Database()
    rabbitmq = RabbitMQ()
    rabbitmq.listen(os.getenv('TRACK_PRODUCT_QUEUE', 'track_product'), callback=start_scraping)


if __name__ == '__main__':
    main()
