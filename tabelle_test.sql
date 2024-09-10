DROP DATABASE if EXISTS MapDB;
CREATE DATABASE if NOT EXISTS MapDB;
USE MapDB;

DROP USER IF EXISTS 'MapUser'@'localhost';
CREATE USER 'MapUser'@'localhost' IDENTIFIED BY 'map';
GRANT SELECT ON MapDB.* TO 'MapUser'@'localhost';

CREATE TABLE MapDB.caratteri (
	C1 char,
	C2 float,
	C3 float,
	C4 float
);

insert into MapDB.caratteri values('a',2,0, 2);
insert into MapDB.caratteri values('f',1,-1, 3);
insert into MapDB.caratteri values('a',3,5, 1.2);
insert into MapDB.caratteri values('n',3,4, 6);
insert into MapDB.caratteri values('f',2,0,2.1);

CREATE TABLE MapDB.vuota (
	R1 float,
	R2 float
);

CREATE TABLE MapDB.exampleTab (
	X1 float,
	X2 float,
	X3 float
);

insert into MapDB.exampleTab values(1,2,0);
insert into MapDB.exampleTab values(0,1,-1);
insert into MapDB.exampleTab values(1,3,5);
insert into MapDB.exampleTab values(1,3,4);
insert into MapDB.exampleTab values(2,2,0);

commit;