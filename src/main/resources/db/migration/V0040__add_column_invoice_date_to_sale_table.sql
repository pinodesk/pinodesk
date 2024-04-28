alter table sale add column invoice_date date after invoice_number;

update sale set invoice_date = created_at; -- Invoice date will still be null here, no "where" clause needed

alter table sale alter column invoice_date set not null;
