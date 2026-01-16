import aiohttp
from dotenv import load_dotenv
from pathlib import Path
import asyncio, os, random


load_dotenv()

TEST_URL = os.getenv('TEST_URL')
PROXIES_SOURCE = os.getenv('PROXIES_SOURCE')

PROJECT_DIR = Path('/home/yuri/Projects/scraper-bot')
PROXIES_FILE = PROJECT_DIR / 'data/proxies.txt'


def get_random_proxy() -> dict[str]:
    with open(PROXIES_FILE, 'r') as file:
        lines: list[str] = file.readlines()

    parts = random.choice(lines).split(':')
    ip, port, username, password = parts

    return {
        'server': f'http://{ip}:{port}',
        'username': username,
        'password': password[:-1]
    }


async def write_file(content: str) -> None:
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
