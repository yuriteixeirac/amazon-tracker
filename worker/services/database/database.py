import logging
import os
import time
from typing import Any

import dotenv
import pymysql

dotenv.load_dotenv()

logger = logging.getLogger(__name__)


class Database:
    def __init__(self):
        self.host = os.getenv('DB_HOST', os.getenv('MYSQL_HOST', 'localhost'))
        self.port = int(os.getenv('DB_PORT', '3306'))
        self.user = os.getenv('DB_USER', os.getenv('MYSQL_USER', 'root'))
        self.password = os.getenv('DB_PASSWORD', os.getenv('MYSQL_ROOT_PASSWORD', ''))
        self.schema = os.getenv('DB_NAME', os.getenv('MYSQL_DATABASE', 'price_tracker'))

        self.connection = self.connect()


    def connect(self) -> pymysql.Connection:
        for attempt in range(1, 11):
            try:
                return pymysql.connect(
                    user=self.user,
                    password=self.password,
                    database=self.schema,
                    host=self.host,
                    port=self.port,
                    charset='utf8mb4',
                    autocommit=False
                )
            except pymysql.MySQLError:
                logger.warning('Database unavailable, retrying connection (%s/10)', attempt)
                time.sleep(min(attempt * 2, 30))

        raise RuntimeError('Could not connect to database')


    def ensure_connection(self) -> None:
        self.connection.ping(reconnect=True)

    
    def insert_record(self, record: dict[str, Any]) -> None:
        product_id = record['productId']

        self.ensure_connection()
        try:
            with self.connection.cursor() as cursor:
                cursor.execute('SELECT id FROM products WHERE id = %s', (product_id,))
                if cursor.fetchone() is None:
                    raise ValueError(f'Product {product_id} does not exist')

                cursor.execute(
                    'UPDATE products SET title = %s, url = %s WHERE id = %s',
                    (record['title'], record['url'], product_id)
                )

                cursor.execute(
                    'INSERT INTO product_records (price, product_id, tracked_at) VALUES (%s, %s, %s)',
                    (record['price'], product_id, record['tracked_at'])
                )

            self.connection.commit()
        except Exception:
            self.connection.rollback()
            raise


    def close(self) -> None:
        if self.connection:
            self.connection.close()
