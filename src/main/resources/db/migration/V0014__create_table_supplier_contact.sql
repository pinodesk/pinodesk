create table if not exists t_supplier_contact (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	supplier_id bigint not null,
	name varchar(256) not null,
	phone varchar(16),
	email varchar(256),
	primary key (id),
	constraint fk_t_supplier_contact__supplier_id foreign key (supplier_id) references t_supplier(id)
);
