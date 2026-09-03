insert into menu (id, parent_menu_id, language, code, name, seq_num) values 
(37, 2, 'en', '0019', 'Consignment', 3),
(38, 15, 'id', '0019', 'Konsinyasi', 3);

update menu set seq_num=4 where code='0009'; -- payables
update menu set seq_num=5 where code='0010'; -- receivables
