create table if not exists t_contact (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	contact_type int not null,
	code varchar(128) not null,
	name varchar(256) not null,
	phone varchar(16),
	email varchar(256),
	address varchar(512),
	company_name varchar(256),
	primary key (id)
);
