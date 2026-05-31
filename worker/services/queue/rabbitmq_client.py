import pika
import os
import time
import logging


logger = logging.getLogger(__name__)


class PermanentMessageError(Exception):
    pass


class RabbitMQ:
    def __init__(self):
        self.username = self.require_env('RABBITMQ_USER')
        self.password = self.require_env('RABBITMQ_PASS')
        self.host = os.getenv('RABBITMQ_HOST', 'localhost')
        self.port = int(os.getenv('RABBITMQ_PORT', '5672'))

        self.connection = None
        self.channel = None

        self.connect()


    def require_env(self, name: str) -> str:
        value = os.getenv(name)
        if not value:
            raise RuntimeError(f'{name} environment variable is required')

        return value


    def connect(self) -> None:
        for attempt in range(1, 11):
            try:
                self.connection = pika.BlockingConnection(
                    pika.ConnectionParameters(
                        self.host, self.port,
                        credentials=pika.PlainCredentials(
                            self.username, self.password, False
                        ),
                        heartbeat=60,
                        blocked_connection_timeout=300
                    )
                )
                self.channel = self.connection.channel()
                return
            except pika.exceptions.AMQPConnectionError:
                logger.warning('RabbitMQ unavailable, retrying connection (%s/10)', attempt)
                time.sleep(min(attempt * 2, 30))

        raise RuntimeError('Could not connect to RabbitMQ')

    
    def listen(self, queue: str, callback) -> None:
        failed_queue = f'{queue}.failed'
        self.channel.queue_declare(queue=queue, durable=True)
        self.channel.queue_declare(queue=failed_queue, durable=True)
        self.channel.basic_qos(prefetch_count=1)

        def wrapped_callback(ch, method, properties, body):
            try:
                callback(body)
                ch.basic_ack(delivery_tag=method.delivery_tag)
            except PermanentMessageError as exception:
                logger.warning('Rejecting invalid message: %s', exception)
                self.publish_failed(failed_queue, body, str(exception))
                ch.basic_ack(delivery_tag=method.delivery_tag)
            except Exception as exception:
                logger.exception('Message processing failed')
                self.publish_failed(failed_queue, body, str(exception))
                ch.basic_ack(delivery_tag=method.delivery_tag)

        self.channel.basic_consume(queue=queue, on_message_callback=wrapped_callback, auto_ack=False)
        self.channel.start_consuming()


    def publish_failed(self, queue: str, body: bytes, error: str) -> None:
        self.channel.basic_publish(
            exchange='',
            routing_key=queue,
            body=body,
            properties=pika.BasicProperties(
                delivery_mode=2,
                content_type='application/json',
                headers={'error': error[:500]}
            )
        )
    

    def close(self) -> None:
        if self.channel and self.channel.is_open:
            self.channel.close()

        if self.connection and self.connection.is_open:
            self.connection.close()
