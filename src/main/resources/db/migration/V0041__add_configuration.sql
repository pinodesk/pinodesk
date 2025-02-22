alter table configuration 
add constraint uk_configuration__code unique (code);

-- set install datetime
insert into configuration (code, "value") 
select 'install_datetime', formatdatetime(min(created_at), 'yyyy-MM-dd''T''HH:mm:ss.SSSXXX') from configuration;

-- set trial period in days
insert into configuration (code, "value", description) values 
('trial_period_days', '60', 'Trial period (no activation) in days'),
('activate_later', 'no', null);
