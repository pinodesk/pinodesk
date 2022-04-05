create table if not exists unit (
	id IDENTITY NOT NULL PRIMARY KEY,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	label varchar(32) not null,
	name varchar(128) not null
);

create index idx_unit__deleted_at on unit (deleted_at);

insert into unit (created_at, updated_at, deleted_at, label, name) values
(current_timestamp, current_timestamp, NULL, 'PCS', 'Pieces'),
(current_timestamp, current_timestamp, NULL, 'BTL', 'Bottle'),
(current_timestamp, current_timestamp, NULL, 'TABLET', 'Tablet'),
(current_timestamp, current_timestamp, NULL, 'CAPS', 'Capsule'),
(current_timestamp, current_timestamp, NULL, 'PACK', 'Pack'),
(current_timestamp, current_timestamp, NULL, 'BOX', 'Box'),
(current_timestamp, current_timestamp, NULL, 'SACHET', 'Sachet'),
(current_timestamp, current_timestamp, NULL, 'CUP', 'Cup'),
(current_timestamp, current_timestamp, NULL, 'JAR', 'Jar'),
(current_timestamp, current_timestamp, NULL, 'STRIP', 'Strip');
