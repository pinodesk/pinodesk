create table if not exists drug (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	product_id bigint not null,
	classification_code char(4),
	indication varchar(512),
	contraindication varchar(512),
	primary key (id),
	index idx_drug__deleted_at (deleted_at),
	index idx_drug__classification_code (classification_code),
	index idx_drug__classification_code__deleted_at (classification_code, deleted_at)
);

-- Insert drugs from products
insert into drug (created_at, updated_at, product_id) 
select now(), now(), id from product where category_code = '000000518';
