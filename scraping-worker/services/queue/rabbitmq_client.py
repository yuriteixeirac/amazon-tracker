import pika
import os


class RabbitMQ:
    def __init__(self):
        self.username = os.getenv('RABBITMQ_USER')
        self.password = os.getenv('RABBITMQ_PASS')
        self.host = os.getenv('RABBITMQ_HOST')
        self.port = 5672

        self.channel = None

        self.connect()


    def connect(self) -> None:
        connection = pika.BlockingConnection(
            pika.ConnectionParameters(
                self.host, self.port,
                credentials=pika.PlainCredentials(
                    self.username, self.password, False
                )
            )
        )
        self.channel = connection.channel()

    
    def listen(self, queue: str, callback) -> None:
        self.channel.queue_declare(queue)
        self.channel.basic_consume(queue, callback)
        self.channel.start_consuming()
    

    def close(self) -> None:
        self.channel.close()
