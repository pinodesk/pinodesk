-- Drop column subtotal from consignment_detail
alter table consignment_detail drop column subtotal;

-- Alter table product_stock
alter table product_stock 
add column consignment_id bigint;

alter table product_stock 
add column consignment_invoice_number varchar(64);

alter table product_stock 
add constraint fk_product_stock__consignment_id 
foreign key (consignment_id) references consignment (id) on delete cascade;

-- Alter table product_expiry
alter table product_expiry
add column consignment_id bigint;

alter table product_expiry
add column consignment_invoice_number varchar(64);

alter table product_expiry 
add constraint fk_product_expiry__consignment_id 
foreign key (consignment_id) references consignment (id) on delete cascade;

-- Alter table product_price
alter table product_price 
add column consignment_id bigint;

alter table product_price 
add column consignment_invoice_number varchar(64);

alter table product_price 
add constraint fk_product_price__consignment_id 
foreign key (consignment_id) references consignment (id) on delete cascade;
