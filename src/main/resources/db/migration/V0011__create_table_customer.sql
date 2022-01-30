create table if not exists customer (
	id IDENTITY NOT NULL PRIMARY KEY,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	code varchar(12) not null, -- e.g. 202103110001
	name varchar(256) not null,
	phone varchar(16),
	email varchar(256),
	address varchar(512)
);

create index idx_customer__deleted_at on customer (deleted_at);
create index idx_customer__code on customer (code);
create index idx_customer__code__deleted_at on customer (code, deleted_at);
