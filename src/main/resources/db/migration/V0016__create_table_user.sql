create table if not exists "user" (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	primary key (id)
);

insert into "user" (created_at, updated_at, deleted_at) values (current_timestamp, current_timestamp, null);
