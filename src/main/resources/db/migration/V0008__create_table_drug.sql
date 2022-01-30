create table if not exists drug (
	id IDENTITY NOT NULL PRIMARY KEY,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	product_id bigint not null,
	drug_category_id bigint not null,
	indication varchar(512),
	contraindication varchar(512),
	constraint fk_drug__product_id foreign key (product_id) references product(id),
	constraint fk_drug__drug_category_id foreign key (drug_category_id) references drug_category(id)
);

create index idx_drug__deleted_at on drug (deleted_at);
