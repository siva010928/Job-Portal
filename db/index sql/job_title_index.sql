create fulltext index Fidx_jobTitle on jobs(title);
select * from jobs where title like "%soft%";
explain select * from jobs where title like "%soft%";

select * from jobs where match(title) against("soft.*" in boolean mode);
select * from jobs where match(title) against("soft.*" in boolean mode) and title like "%soft%";
explain select * from jobs where match(title) against("soft") and title like "%soft%";

show status like "last_query_cost";
