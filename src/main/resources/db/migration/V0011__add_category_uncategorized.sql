insert into t_product_category (id, created_at, updated_at, deleted_at, parent_category_id, language_code, code, name, description) values
(100505833, current_timestamp, current_timestamp, NULL, NULL, 'en', '000505833', 'Uncategorized', NULL),
(200505833, current_timestamp, current_timestamp, NULL, NULL, 'id', '000505833', 'Tidak Dikategorikan', NULL);

update t_product set category_code = '000505833' where category_code IS NULL or category_code = '';
