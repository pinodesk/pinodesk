-- Table: customer
--
create table if not exists customer (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	code varchar(12) not null, -- e.g. 202103110001
	name varchar(256) not null,
	phone varchar(16),
	email varchar(256),
	address varchar(512),
	primary key (id),
	index idx_customer__deleted_at (deleted_at),
	index idx_customer__code (code),
	index idx_customer__code__deleted_at (code, deleted_at)
);

-- Table: configuration
--
create table if not exists configuration (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	code varchar(128) not null,
	"value" varchar(1024) not null,
	description varchar(512),
	primary key (id),
	index idx_configuration__code (code),
	index idx_configuration__deleted_at (deleted_at),
	index idx_configuration__code__deleted_at (code, deleted_at)
);

-- Table: drug_category_base
--
create table if not exists drug_category_base (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	code varchar(64) not null,
	name varchar(256) not null,
	description varchar(512),
	primary key (id),
	index idx_drug_category_base__code (code),
	index idx_drug_category_base__deleted_at (deleted_at),
	index idx_drug_category_base__code__deleted_at (code, deleted_at)
);

-- Table: drug_category
--
create table if not exists drug_category (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	drug_category_base_id bigint not null,
	code varchar(64) not null,
	name varchar(256) not null,
	description varchar(512),
	primary key (id),
	index idx_drug_category__code (code),
	index idx_drug_category__deleted_at (deleted_at),
	index idx_drug_category__code__deleted_at (code, deleted_at),
	constraint fk_drug_category__drug_category_base_id foreign key (drug_category_base_id) references drug_category_base(id)
);

-- Table: unit
--
create table if not exists unit (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	label varchar(32) not null,
	name varchar(128) not null,
	primary key (id),
	index idx_unit__deleted_at (deleted_at)
);

-- Table: product_category
--
create table if not exists product_category (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	parent_category_id bigint,
	language_code char(2) not null,
	code varchar(64) not null,
	name varchar(256) not null,
	description varchar(512),
	primary key (id),
	index idx_product_category__code (code),
	index idx_product_category__deleted_at (deleted_at),
	index idx_product_category__code__deleted_at (code, deleted_at),
	index idx_product_category__language_code__code (language_code, code),
	index idx_product_category__language_code__code__deleted_at (language_code, code, deleted_at)
);

-- Table: product
--
create table if not exists product (
	id bigint not null auto_increment,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	code varchar(64) not null,
	barcode varchar(24),
	name varchar(256) not null,
	description varchar(512),
	quantity integer null,
	unit_id bigint not null,
	unit_label varchar(32) not null,
	category_code varchar(64) not null,
	general_selling_price decimal(12,2) null,
	prescription_selling_price decimal(12,2) null,
    average_buying_price decimal(12,2) null,
	closest_expired_date date null,
	status varchar(8) not null, -- ACTIVE, INACTIVE
	primary key (id),
	constraint fk_product__unit_id foreign key (unit_id) references unit(id),
	index idx_product__deleted_at (deleted_at),
	index idx_product__code (code),
	index idx_product__barcode (barcode),
	index idx_product__category_code (category_code),
	index idx_product__id__unit_id (id, unit_id)
);
