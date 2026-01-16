import pika


class RabbitMQ:
    def __init__(self):
        self.username = 'guest'
        self.password = 'guest'
        self.host = 'localhost'
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
