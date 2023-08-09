alter table purchase alter column discount rename to total_discount;
alter table purchase alter column total_purchase rename to total_price;

alter table purchase_detail add column discount_type varchar(12);      -- e.g. percentage, fixed_amount
alter table purchase_detail add column discount_amount decimal(16,4);  -- total 16 digits, including 4 digits after decimal point
alter table purchase_detail add column buying_price_discount decimal(16,4);
alter table purchase_detail add column subtotal_discount decimal(16,4);
update purchase_detail set subtotal_discount = 0;
alter table purchase_detail alter column subtotal_discount set not null;

alter table purchase_detail alter column subtotal rename to subtotal_price;

-- Update decimal columns

alter table purchase modify total_discount decimal(16,4) null;
alter table purchase modify tax decimal(16,4) null;
alter table purchase modify total_price decimal(16,4) not null;
alter table purchase modify total_payment decimal(16,4) not null;

alter table purchase_detail modify buying_price decimal(16,4) not null;
alter table purchase_detail modify subtotal_price decimal(16,4) not null;

alter table product modify general_selling_price decimal(16,4) null;
alter table product modify prescription_selling_price decimal(16,4) null;
alter table product modify average_buying_price decimal(16,4) null;

alter table sale modify total_sale decimal(16,4) not null;
alter table sale modify total_payment decimal(16,4) not null;

alter table sale_detail modify selling_price decimal(16,4) not null;
alter table sale_detail modify subtotal decimal(16,4) not null;

alter table product_price modify general_selling_price decimal(16,4) null;
alter table product_price modify prescription_selling_price decimal(16,4) null;

alter table payable modify amount decimal(16,4) not null;
alter table payable_payment modify amount decimal(16,4) not null;

alter table receivable modify amount decimal(16,4) not null;
alter table receivable_payment modify amount decimal(16,4) not null;
