create index idx_jobStatus on jobs(job_status);
select * from jobs where job_status="close";
explain select * from jobs use index(idx_jobStatus) where job_status="close";

explain select * from jobs use index(primary) where job_status="close";