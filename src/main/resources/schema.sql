create table if not exists User(
id identity,
username varchar(50) not null,
password varchar(255) not null,
email varchar(50) not null,
street varchar(100) not null,
house varchar(10) not null,
apartment smallint not null,
phone varchar(15) not null
);