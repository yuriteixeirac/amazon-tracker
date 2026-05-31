CREATE TABLE IF NOT EXISTS users (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	email VARCHAR(320) UNIQUE NOT NULL,
	password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS products (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	title VARCHAR(512),
	url VARCHAR(512) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS product_records (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	product_id BIGINT NOT NULL,
	price DECIMAL(10, 2),
	tracked_at DATETIME NOT NULL DEFAULT now(),

	FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE IF NOT EXISTS users_products (
	user_id BIGINT NOT NULL,
	product_id BIGINT NOT NULL,

	PRIMARY KEY (user_id, product_id),
	FOREIGN KEY (user_id) REFERENCES users(id),
	FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE INDEX IF NOT EXISTS idx_product_records_product_id ON product_records(product_id);
CREATE INDEX IF NOT EXISTS idx_product_records_tracked_at ON product_records(tracked_at);
