create table consignment (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp null,
	supplier_id bigint not null,
	invoice_number varchar(64) not null,
	invoice_date date not null,
	total_product int not null,
	user_id bigint not null,
	primary key (id),
	index idx_consignment__deleted_at (deleted_at),
	index idx_consignment__supplier_id (supplier_id),
	index idx_consignment__supplier_id__deleted_at (supplier_id, deleted_at),
	constraint fk_consignment__supplier_id foreign key (supplier_id) references supplier(id),
    constraint fk_consignment__user_id foreign key (user_id) references `user`(id)
);

create table consignment_detail (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp null,
	consignment_id bigint not null,
	product_id bigint not null,
	price decimal(16,4) not null,
	quantity int not null,
	subtotal decimal(16,4) not null,
	primary key (id),
	index idx_consignment_detail__deleted_at (deleted_at),
	index idx_consignment_detail__consignment_id (consignment_id),
	index idx_consignment_detail__consignment_id__deleted_at (consignment_id, deleted_at),
	constraint fk_consignment_detail__consignment_id foreign key (consignment_id) references consignment(id) on delete cascade,
	constraint fk_consignment_detail__product_id foreign key (product_id) references product(id)
);
