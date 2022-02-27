create table if not exists drug_category_base (
	id IDENTITY NOT NULL PRIMARY KEY,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	code varchar(64) not null,
	name varchar(256) not null,
	description varchar(512)
);

create index idx_drug_category_base__deleted_at on drug_category_base(deleted_at);
create index idx_drug_category_base__code on drug_category_base(code);
create index idx_drug_category_base__code__deleted_at on drug_category_base(code, deleted_at);

insert into drug_category_base (id, created_at, updated_at, deleted_at, code, name, description) values
(null, current_timestamp, current_timestamp, null, 'PERMENKESRI', 'Peraturan Menteri Kesehatan Indonesia', 'Permenkes No.917 Tahun 1993'),
(null, current_timestamp, current_timestamp, null, 'USFDA', 'US Food and Drug Administration', 'General drug categories by US FDA');
