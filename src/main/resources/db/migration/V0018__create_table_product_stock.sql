create table if not exists product_stock (
	id IDENTITY NOT NULL PRIMARY KEY,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	product_id bigint not null,
	quantity_in int,
	quantity_out int,
	final_quantity int not null,
	purchase_id bigint,
	purchase_invoice_number varchar(64),
	sale_id bigint,
	sale_invoice_number varchar(64),
	user_id bigint not null,
	activity varchar(128) not null,
	remarks varchar(128),
	constraint fk_product_stock__product_id foreign key (product_id) references product(id),
	constraint fk_product_stock__purchase_id foreign key (purchase_id) references purchase(id)
);

create index idx_product_stock__deleted_at on product_stock (deleted_at);
create index idx_product_stock__product_id on product_stock (product_id);
create index idx_product_stock__product_id__deleted_at on product_stock (product_id, deleted_at);
create index idx_product_stock__purchase_id on product_stock (purchase_id);
create index idx_product_stock__purchase_id__deleted_at on product_stock (purchase_id, deleted_at);
create index idx_product_stock__sale_id on product_stock (sale_id);
create index idx_product_stock__sale_id__deleted_at on product_stock (sale_id, deleted_at);
create index idx_product_stock__user_id on product_stock (user_id);
create index idx_product_stock__user_id__deleted_at on product_stock (user_id, deleted_at);
