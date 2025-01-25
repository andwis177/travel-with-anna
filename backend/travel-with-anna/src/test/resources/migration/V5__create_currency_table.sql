CREATE TABLE IF NOT EXISTS currency_exchange (
    currency_id BIGSERIAL PRIMARY KEY,
    code VARCHAR(10),
    exchange_value  DECIMAL(19, 12) NOT NULL,
    date TIMESTAMP NOT NULL
    );


