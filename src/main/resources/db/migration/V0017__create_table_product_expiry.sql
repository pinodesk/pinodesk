create table if not exists product_expiry (
	id IDENTITY NOT NULL PRIMARY KEY,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	product_id bigint not null,
	expired_date date not null,
	batch_number varchar(64),
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
	constraint fk_product_expiry__product_id foreign key (product_id) references product(id),
	constraint fk_product_expiry__purchase_id foreign key (purchase_id) references purchase(id),
	constraint fk_product_expiry__user_id foreign key (user_id) references user(id)
);

create index idx_product_expiry__deleted_at on product_expiry (deleted_at);
create index idx_product_expiry__product_id on product_expiry (product_id);
create index idx_product_expiry__product_id__deleted_at on product_expiry (product_id, deleted_at);
create index idx_product_expiry__purchase_id on product_expiry (purchase_id);
create index idx_product_expiry__purchase_id__deleted_at on product_expiry (purchase_id, deleted_at);
create index idx_product_expiry__batch_number on product_expiry (batch_number);
create index idx_product_expiry__batch_number__deleted_at on product_expiry (batch_number, deleted_at);
create index idx_product_expiry__expired_date on product_expiry (expired_date);
create index idx_product_expiry__expired_date__deleted_at on product_expiry (expired_date, deleted_at);
create index idx_product_expiry__user_id on product_expiry (user_id);
create index idx_product_expiry__user_id__deleted_at on product_expiry (user_id, deleted_at);
