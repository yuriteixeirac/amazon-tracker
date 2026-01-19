import pymysql, dotenv
import os

dotenv.load_dotenv()


class Database:
    def __init__(self):
        self.host = 'localhost'
        self.port = 3306
        self.user = os.getenv('MYSQL_USER')
        self.password = os.getenv('MYSQL_ROOT_PASSWORD')
        self.schema = os.getenv('MYSQL_DATABASE')

        self.connection = self.connect()
        self.cursor = None
        
        self.get_cursor()


    def connect(self) -> pymysql.Connection:
        return pymysql.connect(
            user=self.user,
            password=self.password,
            database=self.schema,
            host=self.host,
            port=self.port
        )


    def get_cursor(self) -> None:
        self.cursor = self.connection.cursor()

    
    def insert_record(self, record: dict[str]) -> None:
        try:
            self.cursor.execute(
                'INSERT INTO products (title, url) VALUES (%s, %s)',
                (record['title'], record['url'])
            )
            
            product_id = self.cursor.lastrowid

            self.cursor.execute(
                'INSERT INTO product_records (price, product_id, tracked_at) VALUES (%s, %s, %s)',
                (record['price'], product_id, record['tracked_at'])
            )

            user_id = record.get('userId', None)
            if user_id:
                self.cursor.execute(
                    'INSERT INTO users_products (user_id, product_id) VALUES (%s, %s)',
                    (record['userId'], product_id)
                )
        except:
            self.connection.commit()
