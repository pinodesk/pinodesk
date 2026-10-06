-- Create payment_method table with all required columns including ENUM type for category
-- Supported categories: CASH, TRANSFER, QRIS, BANK_CARD, E_WALLET
-- Status defaults to 'active' for new payment methods
-- Normalized name is auto-generated for case-insensitive lookups
-- Default method constraint ensures only Cash can be marked as default
CREATE TABLE payment_method (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    name VARCHAR(100) NOT NULL,
    category ENUM('CASH', 'TRANSFER', 'QRIS', 'BANK_CARD', 'E_WALLET') NOT NULL,
    default_method BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(16) NOT NULL DEFAULT 'active',
    normalized_name VARCHAR(100) GENERATED ALWAYS AS (LOWER(TRIM(name))),
    CONSTRAINT uk_payment_method_name UNIQUE (normalized_name),
    CONSTRAINT ck_payment_method_default CHECK (NOT default_method OR category = 'CASH')
);

-- Insert default Cash payment method
-- Only Cash can be set as default method per business rule
INSERT INTO payment_method (name, category, default_method, status)
VALUES ('Cash', 'CASH', true, 'active');

-- Add payment_method_id column to sale table
ALTER TABLE sale ADD COLUMN payment_method_id BIGINT;

-- Populate payment_method_id for existing sales with the default payment method
UPDATE sale SET payment_method_id = (SELECT id FROM payment_method WHERE default_method = true);

-- Add foreign key constraint to payment_method table
ALTER TABLE sale ADD CONSTRAINT fk_sale_payment_method FOREIGN KEY (payment_method_id) REFERENCES payment_method(id);

-- Add index on payment_method_id for better query performance
ALTER TABLE sale ADD INDEX idx_sale_payment_method (payment_method_id);

-- Add Payment Methods menu item under Master menu (code: 0003)
INSERT INTO menu (parent_menu_id, language, code, name, seq_num)
SELECT id, language, '0020', CASE WHEN language = 'id' THEN 'Metode Pembayaran' ELSE 'Payment Methods' END, 4
FROM menu WHERE code = '0003';

-- Copy user group permissions from Configuration menu (code: 0011) to Payment Methods menu
INSERT INTO user_group_menu (user_group_id, menu_code, read, write)
SELECT user_group_id, '0020', read, write FROM user_group_menu WHERE menu_code = '0011' AND deleted_at IS NULL;
