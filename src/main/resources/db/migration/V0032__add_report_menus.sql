alter table menu
add column seq_num int;

update menu set seq_num=1 where code='0015';
update menu set seq_num=2 where code='0001';
update menu set seq_num=3 where code='0002';
update menu set seq_num=5 where code='0003';
update menu set seq_num=1 where code='0004';
update menu set seq_num=2 where code='0005';
update menu set seq_num=3 where code='0006';
update menu set seq_num=4 where code='0014';
update menu set seq_num=1 where code='0007';
update menu set seq_num=2 where code='0008';
update menu set seq_num=3 where code='0009';
update menu set seq_num=4 where code='0010';
update menu set seq_num=1 where code='0011';
update menu set seq_num=2 where code='0012';
update menu set seq_num=3 where code='0013';

alter table menu alter seq_num set not null;

insert into menu (id, parent_menu_id, language, code, name, seq_num) values 
(31, null, 'en', '0016', 'Reports', 4),
(32, null, 'id', '0016', 'Laporan', 4),
(33, 31, 'en', '0017', 'Sales', 1),
(34, 32, 'id', '0017', 'Penjualan', 1),
(35, 31, 'en', '0018', 'Purchases', 2),
(36, 32, 'id', '0018', 'Pembelian', 2);
