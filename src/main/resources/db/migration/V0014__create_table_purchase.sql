create table if not exists purchase (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	supplier_id bigint not null,
	order_number varchar(64) not null,
	order_date date not null,
	order_status varchar(8) not null, -- DRAFT, ACTIVE, COMPLETE
	total_product int not null,
	total_purchase decimal(12,2) not null,
	total_payment decimal(12,2) not null,
	payment_method varchar(9) not null, -- CASH, ON_CREDIT
	payment_period_count int,
	payment_period_unit varchar(5),	-- DAY, WEEK, MONTH
	payment_due_date date,
	payment_status varchar(6), -- PAID, UNPAID
	discount decimal(12,2) default 0,	
	tax decimal(12,2) default 0,
	primary key (id),
	index idx_purchase__deleted_at (deleted_at),
	index idx_purchase__supplier_id (supplier_id),
	index idx_purchase__supplier_id__deleted_at (supplier_id, deleted_at),
	index idx_purchase__order_number (order_number),
	index idx_purchase__order_number__deleted_at (order_number, deleted_at),
	index idx_purchase__order_date (order_date),
	index idx_purchase__order_date__deleted_at (order_date, deleted_at),
	index idx_purchase__payment_method (payment_method),
	index idx_purchase__payment_method__deleted_at (payment_method, deleted_at),
	index idx_purchase__payment_period_unit (payment_period_unit),
	index idx_purchase__payment_period_unit__deleted_at (payment_period_unit, deleted_at),
	constraint fk_purchase__supplier_id foreign key (supplier_id) references supplier(id)
);

create table if not exists purchase_detail (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	purchase_id bigint not null,
	product_id bigint not null,
	product_price decimal(12,2) not null,
	quantity int not null,
	subtotal decimal(12,2) not null,
	primary key (id),
	index idx_purchase_detail__deleted_at (deleted_at),
	index idx_purchase_detail__purchase_id (purchase_id),
	index idx_purchase_detail__purchase_id__deleted_at (purchase_id, deleted_at),
	constraint fk_purchase_detail__purchase_id foreign key (purchase_id) references purchase(id),
	constraint fk_purchase_detail__product_id foreign key (product_id) references product(id)
);
