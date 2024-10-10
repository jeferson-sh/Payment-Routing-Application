-- Tabela Client
CREATE TABLE clients (
    client_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Tabela Seller (herança de Client)
CREATE TABLE sellers (
    client_id BIGINT PRIMARY KEY,
    date_contract DATE,
    CONSTRAINT fk_seller_client FOREIGN KEY (client_id) REFERENCES clients(client_id)
);

-- Tabela Payment
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    CONSTRAINT fk_payment_client FOREIGN KEY (client_id) REFERENCES clients(client_id)
);

-- Tabela PaymentItem (relacionada com Payment)
CREATE TABLE payment_items (
    payment_item_id BIGSERIAL PRIMARY KEY,
    amount NUMERIC(10, 2) NOT NULL,
    payment_status VARCHAR(255),
    payment_id BIGINT NOT NULL,
    CONSTRAINT fk_payment_item_payment FOREIGN KEY (payment_id) REFERENCES payments(id)
);
