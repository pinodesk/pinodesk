create table if not exists drug_category (
	id IDENTITY NOT NULL PRIMARY KEY,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	drug_category_base_id bigint not null,
	code varchar(64) not null,
	name varchar(256) not null,
	description varchar(512),
	constraint fk_drug_category__drug_category_base_id foreign key (drug_category_base_id) references drug_category_base(id)
);

create index idx_drug_category__deleted_at on drug_category (deleted_at);
create index idx_drug_category__code on drug_category (code);
create index idx_drug_category__code__deleted_at on drug_category (code, deleted_at);

insert into drug_category (created_at, updated_at, deleted_at, drug_category_base_id, code, name, description) values
(current_timestamp, current_timestamp, null, 1, 'PERMENKESRI01', 'Obat Bebas Terbatas', null),
(current_timestamp, current_timestamp, null, 1, 'PERMENKESRI02', 'Obat Bebas', null),
(current_timestamp, current_timestamp, null, 1, 'PERMENKESRI03', 'Obat Keras', null),
(current_timestamp, current_timestamp, null, 1, 'PERMENKESRI04', 'Obat Wajib Apotek (OWA)', null),
(current_timestamp, current_timestamp, null, 1, 'PERMENKESRI05', 'Obat Golongan Narkotika', null),
(current_timestamp, current_timestamp, null, 1, 'PERMENKESRI06', 'Obat Psikotropika', null),
(current_timestamp, current_timestamp, null, 1, 'PERMENKESRI07', 'Obat Herbal & Jamu', null);
