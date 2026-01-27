CREATE TABLE sales (
    id BIGSERIAL PRIMARY KEY,
    sale_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) NOT NULL,
    discount DECIMAL(10,2) DEFAULT 0,
    final_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED')),
    notes TEXT,
    seller_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (seller_id) REFERENCES users(id),
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE sale_items (
    id BIGSERIAL PRIMARY KEY,
    sale_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (sale_id) REFERENCES sales(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    sale_id BIGINT NOT NULL,
    payment_method VARCHAR(20) NOT NULL CHECK (payment_method IN ('CASH', 'PIX', 'DEBIT_CARD', 'CREDIT_CARD')),
    payment_status VARCHAR(20) NOT NULL CHECK (payment_status IN ('PAID', 'PENDING')),
    amount DECIMAL(10,2) NOT NULL,
    payment_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sale_id) REFERENCES sales(id) ON DELETE CASCADE
);

CREATE INDEX idx_sales_seller ON sales(seller_id);
CREATE INDEX idx_sales_customer ON sales(customer_id);
CREATE INDEX idx_sales_date ON sales(sale_date);
CREATE INDEX idx_sales_status ON sales(status);
CREATE INDEX idx_sale_items_sale ON sale_items(sale_id);
CREATE INDEX idx_payments_sale ON payments(sale_id);