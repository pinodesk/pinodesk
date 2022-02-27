create table if not exists product_price (
	id IDENTITY NOT NULL PRIMARY KEY,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	product_id bigint not null,
	general_selling_price decimal(12,2) not null,
	prescription_selling_price decimal(12,2),
	purchase_id bigint,
	purchase_invoice_number varchar(64),
	user_id bigint not null,
	activity varchar(128) not null,
	remarks varchar(128),
	constraint fk_product_price__product_id foreign key (product_id) references product(id),
	constraint fk_product_price__purchase_id foreign key (purchase_id) references purchase(id),
	constraint fk_product_price__user_id foreign key (user_id) references user(id)
);

create index idx_product_price__deleted_at on product_price (deleted_at);
create index idx_product_price__product_id on product_price (product_id);
create index idx_product_price__product_id__deleted_at on product_price (product_id, deleted_at);
create index idx_product_price__purchase_id on product_price (purchase_id);
create index idx_product_price__purchase_id__deleted_at on product_price (purchase_id, deleted_at);
create index idx_product_price__user_id on product_price (user_id);
create index idx_product_price__user_id__deleted_at on product_price (user_id, deleted_at);
