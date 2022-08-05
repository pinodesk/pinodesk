create table if not exists `login` (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	user_id bigint not null,
	login_at timestamp not null,
	logout_at timestamp,
	last_activity varchar(128),
	last_activity_at timestamp,
	primary key (id)
);
