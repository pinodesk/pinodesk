create table if not exists t_customer (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	code varchar(12) not null, -- e.g. 202103110001
	name varchar(256) not null,
	phone varchar(16),
	email varchar(256),
	address varchar(512),
	primary key (id)
);

create table if not exists t_configuration (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	code varchar(128) not null,
	value varchar(1024) not null,
	description varchar(512),
	primary key (id),
	index idx_t_configuration__code (code)
);
