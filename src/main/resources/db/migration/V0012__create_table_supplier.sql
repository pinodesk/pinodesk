create table if not exists supplier (
	id IDENTITY NOT NULL PRIMARY KEY,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	code varchar(12) not null, -- e.g. 202103110001
	name varchar(256) not null,
	phone varchar(16),
	email varchar(256),
	website varchar(256),
	address varchar(512)
);

create index idx_supplier__deleted_at on supplier (deleted_at);
create index idx_supplier__code on supplier (code);
create index idx_supplier__code__deleted_at on supplier (code, deleted_at);
