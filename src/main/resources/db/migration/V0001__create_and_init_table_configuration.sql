create table if not exists t_configuration (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	code varchar(128) not null,
	value varchar(1024) not null,
	description varchar(512),
	primary key (id),
	index idx_t_configuration__code (code)
);

insert into t_configuration (id, created_at, updated_at, deleted_at, code, value, description) values 
(null, current_timestamp, current_timestamp, null, 'language_id', '2', ''),
(null, current_timestamp, current_timestamp, null, 'language_code', 'ind', ''),
(null, current_timestamp, current_timestamp, null, 'store_name', 'Hello Store', ''),
(null, current_timestamp, current_timestamp, null, 'store_address', 'Jakarta, Indonesia', ''),
(null, current_timestamp, current_timestamp, null, 'vat_percentage', '10', ''),
(null, current_timestamp, current_timestamp, null, 'drug_category_base_id', '1', '');
