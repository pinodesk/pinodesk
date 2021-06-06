create table if not exists t_customer (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	code varchar(12) not null, -- e.g. 202103110001
	name varchar(256) not null,
	phone varchar(16),
	email varchar(256),
	address varchar(512),
	primary key (id)
);

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

create table if not exists t_product_category (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	parent_category_id bigint,
	language_id bigint not null,
	code varchar(64) not null,
	name varchar(256) not null,
	description varchar(512),
	primary key (id),
	index idx_t_product_category__code (code),
	index idx_t_product_category__language_id__code (language_id, code),
	constraint fk_t_product_category__language_id foreign key (language_id) references t_language(id)
);
