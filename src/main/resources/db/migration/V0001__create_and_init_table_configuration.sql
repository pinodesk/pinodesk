create table if not exists configuration (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	code varchar(128) not null,
	"value" varchar(1024) not null,
	description varchar(512),
	primary key (id),
	index idx_configuration__code (code),
	index idx_configuration__deleted_at (deleted_at),
	index idx_configuration__code__deleted_at (code, deleted_at)
);

insert into configuration (id, created_at, updated_at, deleted_at, code, "value", description) values 
(null, current_timestamp, current_timestamp, null, 'language_code', 'en', ''),
(null, current_timestamp, current_timestamp, null, 'store_name', 'Hello Store', ''),
(null, current_timestamp, current_timestamp, null, 'store_address', 'Jakarta, Indonesia', '');
