CREATE TABLE expense (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    title VARCHAR(200) NOT NULL,
    expense_category_id BIGINT NOT NULL,
    nominal DECIMAL(19, 4) NOT NULL,
    expense_date DATE NOT NULL,
    payment_method_id BIGINT NOT NULL,
    notes TEXT,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_expense_expense_category FOREIGN KEY (expense_category_id) REFERENCES expense_category(id),
    CONSTRAINT fk_expense_payment_method FOREIGN KEY (payment_method_id) REFERENCES payment_method(id),
    CONSTRAINT fk_expense_user FOREIGN KEY (user_id) REFERENCES `user`(id)
);

INSERT INTO menu (parent_menu_id, language, code, name, seq_num)
SELECT id, language, '0022', CASE WHEN language = 'id' THEN 'Pengeluaran' ELSE 'Expenses' END, 5
FROM menu WHERE code = '0002';

INSERT INTO user_group_menu (user_group_id, menu_code, read, write)
SELECT user_group_id, '0022', read, write FROM user_group_menu WHERE menu_code = '0007' AND deleted_at IS NULL;
