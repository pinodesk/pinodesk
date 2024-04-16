-- Delete payable data with deleted_at not null
-- payable_payment will be deleted by cascade
DELETE FROM payable WHERE deleted_at IS NOT NULL OR purchase_id IN(SELECT id FROM purchase WHERE deleted_at IS NOT NULL);

-- Delete receivable data with deleted_at not null
-- receivable_payment will be deleted by cascade
DELETE FROM receivable WHERE deleted_at IS NOT NULL OR sale_id IN(SELECT id FROM sale WHERE deleted_at IS NOT NULL);

-- Delete purchases with deleted_at not null
-- purchase_detail will be deleted by cascade
DELETE FROM purchase WHERE deleted_at IS NOT NULL;

-- Delete sales with deleted_at not null
-- sale_detail will be deleted by cascade
DELETE FROM sale WHERE deleted_at IS NOT NULL;

-- Delete product_expiry data with deleted_at not null  
DELETE FROM product_expiry
WHERE product_id IN (
    SELECT id FROM product WHERE deleted_at IS NOT NULL 
        AND id NOT IN (SELECT product_id FROM purchase_detail)
        AND id NOT IN (SELECT product_id FROM sale_detail));

-- Delete product_price data with deleted_at not null
DELETE FROM product_price
WHERE product_id IN (
    SELECT id FROM product WHERE deleted_at IS NOT NULL 
        AND id NOT IN (SELECT product_id FROM purchase_detail)
        AND id NOT IN (SELECT product_id FROM sale_detail));

-- Delete product_stock data with deleted_at not null
DELETE FROM product_stock
WHERE product_id IN (
    SELECT id FROM product WHERE deleted_at IS NOT NULL 
        AND id NOT IN (SELECT product_id FROM purchase_detail)
        AND id NOT IN (SELECT product_id FROM sale_detail));

-- Drop and recreate fk_drug__product_id with delete cascade
ALTER TABLE drug
DROP CONSTRAINT IF EXISTS fk_drug__product_id;

ALTER TABLE drug
ADD CONSTRAINT fk_drug__product_id
FOREIGN KEY (product_id)
REFERENCES product (id)
ON DELETE CASCADE;

-- Delete package products with deleted_at not null
DELETE FROM package_detail
WHERE product_id in (
    SELECT id FROM product WHERE deleted_at IS NOT NULL 
        AND id NOT IN (SELECT product_id FROM purchase_detail)
        AND id NOT IN (SELECT product_id FROM sale_detail));

-- Delete product data with deleted_at not null and not in purchase_detail and sale_detail
DELETE FROM product
WHERE deleted_at IS NOT NULL
AND id NOT IN (SELECT product_id FROM purchase_detail)
AND id NOT IN (SELECT product_id FROM sale_detail);
