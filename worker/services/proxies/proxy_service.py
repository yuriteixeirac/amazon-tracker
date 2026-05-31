import aiohttp
from dotenv import load_dotenv
from pathlib import Path
import asyncio, logging, os, random


load_dotenv()

TEST_URL = os.getenv('TEST_URL')
PROXIES_SOURCE = os.getenv('PROXIES_SOURCE')
USE_PROXIES = os.getenv('USE_PROXIES', 'false').lower() == 'true'
PROXIES_FILE = Path(os.getenv('PROXY_FILE', '/worker/data/proxies.txt'))

logger = logging.getLogger(__name__)


def get_random_proxy() -> dict[str] | None:
    if not USE_PROXIES:
        return None

    if not PROXIES_FILE.exists():
        logger.warning('Proxy file %s not found; running without proxy', PROXIES_FILE)
        return None

    with open(PROXIES_FILE, 'r') as file:
        lines: list[str] = [line.strip() for line in file.readlines() if line.strip()]

    if not lines:
        logger.warning('Proxy file %s is empty; running without proxy', PROXIES_FILE)
        return None

    parts = random.choice(lines).split(':')
    if len(parts) != 4:
        logger.warning('Invalid proxy entry format; running without proxy')
        return None

    ip, port, username, password = parts

    return {
        'server': f'http://{ip}:{port}',
        'username': username,
        'password': password
    }


async def write_file(content: str) -> None:
    PROXIES_FILE.parent.mkdir(parents=True, exist_ok=True)
    with open(PROXIES_FILE, 'w') as file:
        file.writelines(content)
        

async def fetch_proxies() -> None:
    async with aiohttp.ClientSession() as session:
        async with session.get(PROXIES_SOURCE, timeout=3) as source_response:
            await write_file(
                await source_response.text()
            )


if __name__ == '__main__':
    asyncio.run(fetch_proxies())
