create table if not exists user_group (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	name varchar(128),
	description varchar(256),
	status varchar(8) not null, -- active, inactive
	primary key (id),
	index idx_user_group__deleted_at (deleted_at)
);

insert into user_group (id, name, status) values 
(1, 'administrator', 'active');
