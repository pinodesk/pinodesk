create table if not exists t_drug_category (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	drug_category_base_id bigint not null,
	code varchar(64) not null,
	name varchar(256) not null,
	description varchar(512),
	primary key (id),
	index idx_t_drug_category__code (code),
	constraint fk_t_drug_category__drug_category_base_id foreign key (drug_category_base_id) references t_drug_category_base(id)
);

insert into public.t_drug_category (id, created_at, updated_at, deleted_at, drug_category_base_id, code, name, description) values
(null, current_timestamp, current_timestamp, null, 1, 'PERMENKESRI01', 'Obat Bebas Terbatas', null),
(null, current_timestamp, current_timestamp, null, 1, 'PERMENKESRI02', 'Obat Bebas', null),
(null, current_timestamp, current_timestamp, null, 1, 'PERMENKESRI03', 'Obat Keras', null),
(null, current_timestamp, current_timestamp, null, 1, 'PERMENKESRI04', 'Obat Wajib Apotek (OWA)', null),
(null, current_timestamp, current_timestamp, null, 1, 'PERMENKESRI05', 'Obat Golongan Narkotika', null),
(null, current_timestamp, current_timestamp, null, 1, 'PERMENKESRI06', 'Obat Psikotropika', null),
(null, current_timestamp, current_timestamp, null, 1, 'PERMENKESRI07', 'Obat Herbal & Jamu', null);
