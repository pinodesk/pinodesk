create table if not exists t_language (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	code varchar(3) not null,
	name varchar(128) not null,
	primary key (id),
	index idx_t_language__code (code)
);

insert into t_language (id, created_at, updated_at, deleted_at, code, name) values
(null, current_timestamp, current_timestamp, null, 'eng', 'English'),
(null, current_timestamp, current_timestamp, null, 'ind', 'Bahasa Indonesia');
