-- Drop and recreate fk_product_expiry__purchase_id with delete cascade
ALTER TABLE product_expiry
DROP CONSTRAINT IF EXISTS fk_product_expiry__purchase_id;

ALTER TABLE product_expiry
ADD CONSTRAINT fk_product_expiry__purchase_id
FOREIGN KEY (purchase_id)
REFERENCES purchase (id)
ON DELETE CASCADE;

-- Drop and recreate fk_product_stock__purchase_id with delete cascade
ALTER TABLE product_stock
DROP CONSTRAINT IF EXISTS fk_product_stock__purchase_id;

ALTER TABLE product_stock
ADD CONSTRAINT fk_product_stock__purchase_id
FOREIGN KEY (purchase_id)
REFERENCES purchase (id)
ON DELETE CASCADE;

-- Drop and recreate fk_product_price__purchase_id with delete cascade
ALTER TABLE product_price
DROP CONSTRAINT IF EXISTS fk_product_price__purchase_id;

ALTER TABLE product_price
ADD CONSTRAINT fk_product_price__purchase_id
FOREIGN KEY (purchase_id)
REFERENCES purchase (id)
ON DELETE CASCADE;

-- Drop and recreate fk_payable_payment__payable_id with delete cascade
ALTER TABLE payable_payment
DROP CONSTRAINT IF EXISTS fk_payable_payment__payable_id;

ALTER TABLE payable_payment
ADD CONSTRAINT fk_payable_payment__payable_id
FOREIGN KEY (payable_id)
REFERENCES payable (id)
ON DELETE CASCADE;

-- Drop and recreate fk_receivable_payment__receivable_id with delete cascade
ALTER TABLE receivable_payment
DROP CONSTRAINT IF EXISTS fk_receivable_payment__receivable_id;

ALTER TABLE receivable_payment
ADD CONSTRAINT fk_receivable_payment__receivable_id
FOREIGN KEY (receivable_id)
REFERENCES receivable (id)
ON DELETE CASCADE;

-- Drop and recreate fk_product_expiry__sale_id with delete cascade
ALTER TABLE product_expiry
DROP CONSTRAINT IF EXISTS fk_product_expiry__sale_id;

ALTER TABLE product_expiry
ADD CONSTRAINT fk_product_expiry__sale_id
FOREIGN KEY (sale_id)
REFERENCES sale (id)
ON DELETE CASCADE;

-- Drop and recreate fk_product_stock__sale_id with delete cascade
ALTER TABLE product_stock
DROP CONSTRAINT IF EXISTS fk_product_stock__sale_id;

ALTER TABLE product_stock
ADD CONSTRAINT fk_product_stock__sale_id
FOREIGN KEY (sale_id)
REFERENCES sale (id)
ON DELETE CASCADE;

-- Drop and recreate fk_product_price__sale_id with delete cascade
ALTER TABLE product_price
DROP CONSTRAINT IF EXISTS fk_product_price__sale_id;

ALTER TABLE product_price
ADD CONSTRAINT fk_product_price__sale_id
FOREIGN KEY (sale_id)
REFERENCES sale (id)
ON DELETE CASCADE;

-- Drop and recreate fk_purchase_detail__purchase_id with delete cascade
ALTER TABLE purchase_detail
DROP CONSTRAINT IF EXISTS fk_purchase_detail__purchase_id;

ALTER TABLE purchase_detail
ADD CONSTRAINT fk_purchase_detail__purchase_id
FOREIGN KEY (purchase_id)
REFERENCES purchase (id)
ON DELETE CASCADE;

-- Drop and recreate fk_sale_detail__sale_id with delete cascade
ALTER TABLE sale_detail
DROP CONSTRAINT IF EXISTS fk_sale_detail__sale_id;

ALTER TABLE sale_detail
ADD CONSTRAINT fk_sale_detail__sale_id
FOREIGN KEY (sale_id)
REFERENCES sale (id)
ON DELETE CASCADE;
