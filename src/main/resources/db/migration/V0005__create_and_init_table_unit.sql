create table if not exists t_unit (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	label varchar(32) not null,
	name varchar(128) not null,
	primary key (id)
);

insert into t_unit (id, created_at, updated_at, deleted_at, label, name) values
(null, current_timestamp, current_timestamp, NULL, 'PCS', 'Pieces'),
(null, current_timestamp, current_timestamp, NULL, 'BTL', 'Bottle'),
(null, current_timestamp, current_timestamp, NULL, 'TABLET', 'Tablet'),
(null, current_timestamp, current_timestamp, NULL, 'CAPS', 'Capsule'),
(null, current_timestamp, current_timestamp, NULL, 'PACK', 'Pack'),
(null, current_timestamp, current_timestamp, NULL, 'BOX', 'Box'),
(null, current_timestamp, current_timestamp, NULL, 'SACHET', 'Sachet'),
(null, current_timestamp, current_timestamp, NULL, 'CUP', 'Cup'),
(null, current_timestamp, current_timestamp, NULL, 'JAR', 'Jar'),
(null, current_timestamp, current_timestamp, NULL, 'STRIP', 'Strip');
