create table if not exists purchase (
	id bigint not null auto_increment,
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
	primary key (id),
	index idx_purchase__deleted_at (deleted_at),
	index idx_purchase__supplier_id (supplier_id),
	index idx_purchase__supplier_id__deleted_at (supplier_id, deleted_at),
	index idx_purchase__invoice_number (invoice_number),
	index idx_purchase__invoice_number__deleted_at (invoice_number, deleted_at),
	index idx_purchase__invoice_date (invoice_date),
	index idx_purchase__invoice_date__deleted_at (invoice_date, deleted_at),
	constraint fk_purchase__supplier_id foreign key (supplier_id) references supplier(id)
);

create table if not exists purchase_detail (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	purchase_id bigint not null,
	product_id bigint not null,
	buying_price decimal(12,2) not null,
	quantity int not null,
	subtotal decimal(12,2) not null,
	primary key (id),
	index idx_purchase_detail__deleted_at (deleted_at),
	index idx_purchase_detail__purchase_id (purchase_id),
	index idx_purchase_detail__purchase_id__deleted_at (purchase_id, deleted_at),
	constraint fk_purchase_detail__purchase_id foreign key (purchase_id) references purchase(id),
	constraint fk_purchase_detail__product_id foreign key (product_id) references product(id)
);
