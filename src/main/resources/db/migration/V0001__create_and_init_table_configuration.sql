create table if not exists configuration (
	id IDENTITY NOT NULL PRIMARY KEY,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp on update current_timestamp,
	deleted_at timestamp,
	code varchar(128) not null,
	"value" varchar(1024) not null,
	description varchar(512)
);

CREATE INDEX idx_configuration__deleted_at ON configuration(deleted_at);
CREATE INDEX idx_configuration__code ON configuration(code);
CREATE INDEX idx_configuration__code__deleted_at ON configuration(code, deleted_at);

insert into configuration (created_at, updated_at, deleted_at, code, "value", description) values 
(current_timestamp, current_timestamp, null, 'language_code', 'en', ''),
(current_timestamp, current_timestamp, null, 'store_name', 'Hello Store', ''),
(current_timestamp, current_timestamp, null, 'store_address', 'Jakarta, Indonesia', ''),
(current_timestamp, current_timestamp, null, 'vat_percentage', '10', ''),
(current_timestamp, current_timestamp, null, 'drug_category_base_id', '1', '');
