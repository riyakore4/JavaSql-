create database java1;
use java1;
create table components(
    Sr_no int not null,
    C_name varchar(100) not null,
    Quantity int,
    primary key(C_name));
insert into components(Sr_no,C_name,Quantity)
values(1,'Water flow sensor',40),(2,'TPS',30),(3,'UNO R3 Board',30),(4,'Lithium ion 18650 Battery',40),(5,'Sunboard sheet',20),
      (6,'Bulb Holder',10),(7,'Breadboard',50),(8,'Node MCU',30),(9,'Servo Motor',25),(10,'IR Sensor',35),(11,'Soil Moisture Sensor',10),
      (12,'3.7V18650 Battery',30),(13,'GSM Modern with antenna',10),(14,'Philips LED bulb',20),(15,'2YME Press button',10),
      (16,'Screw driver set',20),(17,'Vinyl Tube Flexible',10),(18,'3.7V 3200mh2 battery',40),(19,'Water motor pump',10),(20,'Buzzer',30);
select * from components;
select sum(Quantity) from components;
Create table group5(
    G_No int not null,
    PRN int not null,
    f_Name varchar(250) not null,
    l_Name varchar(250) not null,
    className varchar(250) not null,
    Issue_Date varchar(250),Return_Date varchar(250),
    mo_no bigint,email varchar(250),
    C_name varchar(250),
    Quantity int,
    primary key(G_No),
    foreign key(C_name) references components (C_name),
    unique(PRN));
select * from group5;
delete from group5;