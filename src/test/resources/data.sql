-- Inserts de exemplo para clients
INSERT INTO clients (name) VALUES ('John Doe');
INSERT INTO clients (name) VALUES ('Jane Smith');

-- Inserts de exemplo para sellers
INSERT INTO sellers (client_id, date_contract) VALUES (1, '2023-01-15'); -- Referenciando client_id de 'John Doe'

-- Inserts de exemplo para payments
INSERT INTO payments (client_id) VALUES (1); -- Pagamento relacionado ao seller 'John Doe'
INSERT INTO payments (client_id) VALUES (2); -- Pagamento relacionado ao cliente 'Jane Smith'

-- Inserts de exemplo para payment_items
INSERT INTO payment_items (amount, payment_id) VALUES (100.00, 1); -- Item para o pagamento 1 (John Doe)
INSERT INTO payment_items (amount, payment_id) VALUES (50.00, 2); -- Item para o pagamento 2 (Jane Smith)
INSERT INTO payment_items (amount, payment_id) VALUES (150.00, 1); -- Outro item para o pagamento 1 (John Doe)
