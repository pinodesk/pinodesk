alter table unit add column `language` char(2);
alter table unit add column `code` char(4);

update unit set language='en', code='0001' where id=1;
update unit set language='en', code='0002' where id=2;
update unit set language='en', code='0003', name='Carton', label='CARTON' where id=3;
update unit set language='en', code='0004' where id=4;
update unit set language='en', code='0005' where id=5;
update unit set language='en', code='0006' where id=6;
update unit set language='en', code='0007' where id=7;
update unit set language='en', code='0008' where id=8;
update unit set language='en', code='0009', name='Can' where id=9;
update unit set language='en', code='0010' where id=10;
update unit set language='en', code='0011' where id=11;
update unit set language='en', code='0012' where id=12;
update unit set language='en', code='0013' where id=13;
update unit set language='en', code='0014' where id=14;
update unit set language='en', code='0015' where id=15;
update unit set language='en', code='0016' where id=16;

alter table unit alter column language set not null;
alter table unit alter column code set not null;

create index idx_unit__language__code on unit(language, code);
create index idx_unit__language__code__deleted_at on unit(language, code, deleted_at);

insert into unit (id, created_at, updated_at, deleted_at, language, code, label, name) values
(17, current_timestamp, current_timestamp, NULL, 'id', '0001', 'PCS', 'Buah'),
(18, current_timestamp, current_timestamp, NULL, 'id', '0002', 'BOX', 'Kotak'),
(19, current_timestamp, current_timestamp, NULL, 'id', '0003', 'CARTON', 'Kardus'),
(20, current_timestamp, current_timestamp, NULL, 'id', '0004', 'BTL', 'Botol'),
(21, current_timestamp, current_timestamp, NULL, 'id', '0005', 'CAPS', 'Kapsul'),
(22, current_timestamp, current_timestamp, NULL, 'id', '0006', 'TAB', 'Tablet'),
(23, current_timestamp, current_timestamp, NULL, 'id', '0007', 'TUB', 'Tube'),
(24, current_timestamp, current_timestamp, NULL, 'id', '0008', 'AMP', 'Ampul'),
(25, current_timestamp, current_timestamp, NULL, 'id', '0009', 'CAN', 'Kaleng'),
(26, current_timestamp, current_timestamp, NULL, 'id', '0010', 'BUNDLE', 'Bundel'),
(27, current_timestamp, current_timestamp, NULL, 'id', '0011', 'PACK', 'Pak'),
(28, current_timestamp, current_timestamp, NULL, 'id', '0012', 'SUPP', 'Suppository'),
(29, current_timestamp, current_timestamp, NULL, 'id', '0013', 'SACHET', 'Saset'),
(30, current_timestamp, current_timestamp, NULL, 'id', '0014', 'CUP', 'Cangkir'),
(31, current_timestamp, current_timestamp, NULL, 'id', '0015', 'JAR', 'Stoples'),
(32, current_timestamp, current_timestamp, NULL, 'id', '0016', 'STRIP', 'Strip');

alter table product add column unit_code char(4);

update product set unit_code=(select u.code from unit u where u.id = product.unit_id);

alter table product alter column unit_code set not null;
drop index idx_product__id__unit_id;
alter table product drop column unit_id;
alter table product drop column unit_label;
