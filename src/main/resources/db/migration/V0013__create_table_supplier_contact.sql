create table if not exists supplier_contact (
	id IDENTITY NOT NULL PRIMARY KEY,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	supplier_id bigint not null,
	name varchar(256) not null,
	phone varchar(16),
	email varchar(256),
	constraint fk_supplier_contact__supplier_id foreign key (supplier_id) references supplier(id)
);

create index idx_supplier_contact__deleted_at on supplier_contact (deleted_at);
create index idx_supplier_contact__supplier_id on supplier_contact (supplier_id);
create index idx_supplier_contact__supplier_id__deleted_at on supplier_contact (supplier_id, deleted_at);
