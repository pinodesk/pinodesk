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

create table if not exists t_unit (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	label varchar(32) not null,
	name varchar(128) not null,
	primary key (id)
);

create table if not exists t_rack (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	code varchar(64) not null,
	name varchar(256) not null,
	description varchar(512),
	primary key (id)
);

create table if not exists t_product (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	code varchar(64) not null,
	barcode varchar(24),
	name varchar(256) not null,
	description varchar(512),
	quantity integer not null default 0,
	unit_id bigint not null,
	unit_label varchar(32) not null,
	category_code varchar(64) not null,
	purchase_price decimal(12,2) not null default 0,
	selling_price decimal(12,2) not null default 0,
    vat_included char(3),
	rack_id bigint,
	rack_code varchar(64),
	rack_name varchar(256),
	expired_date date,
	primary key (id),
	constraint fk_t_product__unit_id foreign key (unit_id) references t_unit(id),
	constraint fk_t_product__rack_id foreign key (rack_id) references t_rack(id),
	index idx_t_product__code (code),
	index idx_t_product__barcode (barcode),
	index idx_t_product__category_code (category_code),
	index idx_t_product__id__unit_id (id, unit_id)
);
