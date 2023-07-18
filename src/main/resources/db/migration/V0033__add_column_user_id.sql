alter table sale add column user_id bigint;
update sale set user_id = (
    select ps.user_id from product_stock ps where ps.sale_id = sale.id order by ps.id limit 1) 
    where user_id is null;
alter table sale alter column user_id set not null;
alter table sale add constraint if not exists fk_sale__user_id foreign key (user_id) references `user`(id);

alter table purchase add column user_id bigint;
update purchase set user_id = (
    select ps.user_id from product_stock ps where ps.purchase_id = purchase.id order by ps.id limit 1) 
    where user_id is null;
alter table purchase alter column user_id set not null;
alter table purchase add constraint if not exists fk_purchase__user_id foreign key (user_id) references `user`(id);

-- Fix other table foreign keys

alter table payable add constraint if not exists fk_payable__purchase_id foreign key (purchase_id) references purchase(id);
alter table payable add constraint if not exists fk_payable__supplier_id foreign key (supplier_id) references supplier(id);
alter table payable_payment add constraint if not exists fk_payable_payment__payable_id foreign key (payable_id) references payable(id);

alter table receivable add constraint if not exists fk_receivable__purchase_id foreign key (sale_id) references sale(id);
alter table receivable add constraint if not exists fk_receivable__supplier_id foreign key (customer_id) references customer(id);
alter table receivable_payment add constraint if not exists fk_receivable_payment__receivable_id foreign key (receivable_id) references receivable(id);

alter table sale add constraint if not exists fk_sale__doctor_id foreign key (doctor_id) references doctor(id);

alter table drug add constraint if not exists fk_drug__product_id foreign key (product_id) references product(id);

alter table user_group_menu add constraint if not exists fk_user_group_menu__user_group_id foreign key (user_group_id) references user_group(id);

alter table menu add constraint if not exists fk_menu__parent_menu_id foreign key (parent_menu_id) references menu(id);

alter table `session` add constraint if not exists fk_session__user_id foreign key (user_id) references `user`(id);

alter table `user` add constraint if not exists fk_user__user_group_id foreign key (user_group_id) references user_group(id);

alter table supplier_contact add constraint if not exists fk_supplier_contact__supplier_id foreign key (supplier_id) references supplier(id);

alter table product_expiry add constraint if not exists fk_product_expiry__sale_id foreign key (sale_id) references sale(id);

alter table product_price add constraint if not exists fk_product_price__sale_id foreign key (sale_id) references sale(id);

alter table product_stock add constraint if not exists fk_product_stock__sale_id foreign key (sale_id) references sale(id);
