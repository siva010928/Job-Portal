use job_portal;
show indexes in languages;
create fulltext index idx_languageName on languages(name);

select * from languages where name like "%tam%";
explain select * from languages where name like "%tam%";
select *,match(name) against("tam.*" in boolean mode) as matching_factor from languages where match(name) against("tam.*" in boolean mode);
select * from languages  where match(name) against("tam.*" in boolean mode) AND name like '%tam%';
explain select * from languages  where match(name) against("tam.*" in boolean mode) AND name like '%tam%';
show status like "last_query_cost";