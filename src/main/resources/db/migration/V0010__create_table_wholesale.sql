create table if not exists wholesale (
    id bigint not null auto_increment,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp on update current_timestamp,
    deleted_at timestamp,
    product_id bigint not null,
    purchase_quantity integer not null,
    selling_price decimal(12,2) not null,
    primary key (id),
	index idx_wholesale__deleted_at (deleted_at),
	index idx_wholesale__product_id (product_id),
	index idx_wholesale__product_id__deleted_at (product_id, deleted_at),
	constraint fk_wholesale__product_id foreign key (product_id) references product(id)
);
