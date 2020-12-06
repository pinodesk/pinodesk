create table if not exists t_product_price (
    id bigint not null auto_increment,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp on update current_timestamp,
    deleted_at timestamp,
    product_id bigint not null,
    purchase_quantity integer default 1,
    selling_price decimal default 0,
    vat_included char(1),
    primary key (id)
);
