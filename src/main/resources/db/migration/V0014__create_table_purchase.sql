create table if not exists purchase (
	id IDENTITY NOT NULL PRIMARY KEY,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	supplier_id bigint not null,
	invoice_number varchar(64) not null,
	invoice_date date not null,
	payment_status varchar(6), -- PAID, UNPAID
	payment_due_date date,
	discount decimal(12,2),	
	tax decimal(12,2),
	total_product int not null,
	total_purchase decimal(12,2) not null,
	total_payment decimal(12,2) not null,
	constraint fk_purchase__supplier_id foreign key (supplier_id) references supplier(id)
);

create index idx_purchase__deleted_at on purchase (deleted_at);
create index idx_purchase__supplier_id on purchase (supplier_id);
create index idx_purchase__supplier_id__deleted_at on purchase (supplier_id, deleted_at);
create index idx_purchase__invoice_number on purchase (invoice_number);
create index idx_purchase__invoice_number__deleted_at on purchase (invoice_number, deleted_at);
create index idx_purchase__invoice_date on purchase (invoice_date);
create index idx_purchase__invoice_date__deleted_at on purchase (invoice_date, deleted_at);

create table if not exists purchase_detail (
	id IDENTITY NOT NULL PRIMARY KEY,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	purchase_id bigint not null,
	product_id bigint not null,
	buying_price decimal(12,2) not null,
	quantity int not null,
	subtotal decimal(12,2) not null,
	constraint fk_purchase_detail__purchase_id foreign key (purchase_id) references purchase(id),
	constraint fk_purchase_detail__product_id foreign key (product_id) references product(id)
);

create index idx_purchase_detail__deleted_at on purchase_detail (deleted_at);
create index idx_purchase_detail__purchase_id on purchase_detail (purchase_id);
create index idx_purchase_detail__purchase_id__deleted_at on purchase_detail (purchase_id, deleted_at);
