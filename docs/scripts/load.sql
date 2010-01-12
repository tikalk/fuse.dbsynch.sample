delete from collector.dest_table;
delete from producer.source_table;
insert into producer.source_table values(1,'desc1');
insert into producer.source_table values(2,'desc2');
insert into producer.source_table values(3,'desc3');
insert into producer.source_table values(4,'desc4');
insert into producer.source_table values(5,'desc5');
insert into producer.source_table values(6,'desc6');
commit;