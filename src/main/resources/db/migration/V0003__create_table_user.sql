create table if not exists "user" (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	user_group_id bigint not null,
	full_name varchar(128),
	username varchar(64) not null,
	password_hash varchar(64) not null,
	status varchar(8) not null, -- active, inactive
	primary key (id),
	index idx_user__deleted_at (deleted_at),
	index idx_user__username (username),
	index idx_user__username__deleted_at (username, deleted_at)
);
