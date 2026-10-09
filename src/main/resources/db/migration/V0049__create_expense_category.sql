CREATE TABLE expense_category (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    name VARCHAR(100) NOT NULL,
    normalized_name VARCHAR(100) GENERATED ALWAYS AS (LOWER(TRIM(name))),
    status ENUM('active', 'inactive') NOT NULL DEFAULT 'active',
    CONSTRAINT uk_expense_category_name UNIQUE (normalized_name),
    CONSTRAINT ck_expense_category_status CHECK (status IN ('active', 'inactive'))
);

INSERT INTO expense_category (name, status) VALUES
    ('Listrik & Air', 'active'),
    ('Sewa', 'active'),
    ('Gaji', 'active'),
    ('Transport', 'active'),
    ('Operasional', 'active'),
    ('Lainnya', 'active');

INSERT INTO menu (parent_menu_id, language, code, name, seq_num)
SELECT id, language, '0021', CASE WHEN language = 'id' THEN 'Kategori Pengeluaran' ELSE 'Expense Categories' END, 6
FROM menu WHERE code = '0003';

INSERT INTO user_group_menu (user_group_id, menu_code, read, write)
SELECT user_group_id, '0021', read, write FROM user_group_menu WHERE menu_code = '0011' AND deleted_at IS NULL;
