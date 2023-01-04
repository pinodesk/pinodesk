insert into product_category (id, created_at, updated_at, deleted_at, parent_category_id, language, code, name) values
(100505834, current_timestamp, current_timestamp, null, null, 'en', '000505834', 'Custom Package'),
(200505834, current_timestamp, current_timestamp, null, null, 'id', '000505834', 'Paket Khusus');

create table if not exists package_detail (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	package_product_id bigint not null,
	product_id bigint not null,
	quantity int not null,
	primary key (id),
	index idx_package_detail__deleted_at (deleted_at),
	index idx_package_detail__package_product_id (package_product_id),
	index idx_package_detail__package_product_id__deleted_at (package_product_id, deleted_at),
	constraint fk_package_detail__product_id foreign key (product_id) references product(id),
	constraint fk_package_detail__package_product_id foreign key (package_product_id) references product(id)
);
