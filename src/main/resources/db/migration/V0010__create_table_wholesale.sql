create table if not exists t_wholesale (
    id bigint not null auto_increment,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp on update current_timestamp,
    deleted_at timestamp,
    product_id bigint not null,
    purchase_quantity integer not null,
    selling_price decimal(12,2) not null,
    primary key (id),
	constraint fk_t_wholesale__product_id foreign key (product_id) references t_product(id)
);
