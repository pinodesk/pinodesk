create table if not exists payable (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	supplier_id bigint not null,
	purchase_id bigint not null,
	invoice_number varchar(64) not null,
	invoice_date date not null,
	payment_due_date date not null,
	payment_amount decimal(12,2) not null,
	payment_date date,
	remarks varchar(128),
	primary key (id),
	index idx_payable__deleted_at (deleted_at),
	index idx_payable__purchase_id (purchase_id),
	index idx_payable__purchase_id__deleted_at (purchase_id, deleted_at)
);
