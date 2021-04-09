create table if not exists t_drug_category_base (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	code varchar(64) not null,
	name varchar(256) not null,
	description varchar(512),
	primary key (id),
	index idx_t_drug_category_base__code (code)
);

insert into t_drug_category_base (id, created_at, updated_at, deleted_at, code, name, description) values
(null, current_timestamp, current_timestamp, null, 'PERMENKESRI', 'Peraturan Menteri Kesehatan Indonesia', 'Permenkes No.917 Tahun 1993'),
(null, current_timestamp, current_timestamp, null, 'USFDA', 'US Food and Drug Administration', 'General drug categories by US FDA');
