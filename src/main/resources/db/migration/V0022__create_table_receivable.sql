create table if not exists receivable (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	customer_id bigint not null,
	sale_id bigint not null,
	invoice_number varchar(64) not null,
	invoice_date date not null,
	due_date date not null,
	amount decimal(12,2) not null,
	completion_date date,
	remarks varchar(128),
	primary key (id),
	index idx_receivable__deleted_at (deleted_at),
	index idx_receivable__sale_id (sale_id),
	index idx_receivable__sale_id__deleted_at (sale_id, deleted_at)
);

create table if not exists receivable_payment (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	receivable_id bigint not null,
	amount decimal(12,2) not null,
	payment_date date not null,
	primary key (id),
	index idx_receivable_payment__deleted_at (deleted_at),
	index idx_receivable_payment__receivable_id (receivable_id)
);
