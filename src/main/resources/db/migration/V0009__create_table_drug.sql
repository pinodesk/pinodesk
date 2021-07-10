create table if not exists drug (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	product_id bigint not null,
	drug_category_id bigint not null,
	drug_category_code varchar(64) not null,
	drug_category_name varchar(256) not null,
	indication varchar(512),
	contraindication varchar(512),
	prescription_price decimal(12,2),
	primary key (id),
	index idx_drug__deleted_at (deleted_at),
	constraint fk_drug__product_id foreign key (product_id) references product(id),
	constraint fk_drug__drug_category_id foreign key (drug_category_id) references drug_category(id)
);
