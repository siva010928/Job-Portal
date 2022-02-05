use job_portal;
show indexes in key_skills;
create fulltext index idx_skillName on key_skills(name);
select * from key_skills where name like "%web dev%";
explain select * from key_skills where name like "%web dev%";
select *,match(name) against("web dev.*" in boolean mode) as matching_factor from key_skills where match(name) against("web dev.*" in boolean mode);
select * from key_skills  where match(name) against(".*web dev.*" in boolean mode) AND name like '%web dev%';
explain select * from key_skills  where match(name) against(".*web dev.*" in boolean mode) AND name like '%web dev%';
show status like "last_query_cost";