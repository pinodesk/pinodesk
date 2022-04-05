create table if not exists sale (
	id IDENTITY NOT NULL PRIMARY KEY,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp
);

insert into sale (created_at, updated_at, deleted_at) values (current_timestamp, current_timestamp, null);
