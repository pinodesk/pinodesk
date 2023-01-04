-- Adding multiple columns using H2 syntax, instead of MySQL
alter table doctor
add column (
    phone varchar(16),
    email varchar(256),
    address varchar(512));
