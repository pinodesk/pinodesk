create table if not exists user_group_menu (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	user_group_id bigint not null,
	menu_code bigint not null,
	read varchar(3) not null, -- yes, no
	write varchar(3) not null, -- yes, no
	primary key (id),
	index idx_user_group_menu__deleted_at (deleted_at),
	index idx_user_group_menu__user_group_id (user_group_id),
	index idx_user_group_menu__user_group_id__deleted_at (user_group_id, deleted_at),
	index idx_user_group_menu__user_group_id__menu_code (user_group_id, menu_code),
	index idx_user_group_menu__user_group_id__menu_code__deleted_at (user_group_id, menu_code, deleted_at)
);

insert into user_group_menu (user_group_id, menu_code, read, write) values 
(1, '0011', 'yes', 'yes'),
(1, '0012', 'yes', 'yes'),
(1, '0013', 'yes', 'yes');
