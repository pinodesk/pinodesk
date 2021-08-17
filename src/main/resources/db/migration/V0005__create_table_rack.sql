create table if not exists rack (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	code varchar(64) not null,
	name varchar(256) not null,
	description varchar(512),
	primary key (id),
	index idx_rack__deleted_at (deleted_at)
);
