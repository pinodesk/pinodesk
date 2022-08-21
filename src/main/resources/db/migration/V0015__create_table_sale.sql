create table if not exists sale (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	customer_id bigint,
	doctor_id bigint,
	selling_mode varchar(12) not null, -- PRESCRIPTION, GENERAL
	invoice_number varchar(64) not null,
	payment_status varchar(6) not null, -- PAID, UNPAID
	payment_due_date date,
	total_product int not null,
	total_sale decimal(12,2) not null,
	total_payment decimal(12,2) not null,
	primary key (id),
	index idx_sale__deleted_at (deleted_at),
	index idx_sale__customer_id (customer_id),
	index idx_sale__doctor_id (doctor_id),
	index idx_sale__customer_id__deleted_at (customer_id, deleted_at),
	index idx_sale__invoice_number (invoice_number),
	index idx_sale__invoice_number__deleted_at (invoice_number, deleted_at),
	constraint fk_sale__customer_id foreign key (customer_id) references customer(id)
);

create table if not exists sale_detail (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	sale_id bigint not null,
	product_id bigint not null,
	selling_price decimal(12,2) not null,
	quantity int not null,
	subtotal decimal(12,2) not null,
	primary key (id),
	index idx_sale_detail__deleted_at (deleted_at),
	index idx_sale_detail__sale_id (sale_id),
	index idx_sale_detail__sale_id__deleted_at (sale_id, deleted_at),
	constraint fk_sale_detail__sale_id foreign key (sale_id) references sale(id),
	constraint fk_sale_detail__product_id foreign key (product_id) references product(id)
);
