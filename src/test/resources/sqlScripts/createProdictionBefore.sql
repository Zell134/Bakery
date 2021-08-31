delete from type;
delete from production;

insert into type (id, name)
values(1, 'type1');

insert into production (active, description, image_url, name, price, type)
values(true, 'description1', 'image_url1', 'name1', 1, 1);

insert into production (active, description, image_url, name, price, type)
values(true, 'description2', 'image_url2', 'name2', 2, 1);

insert into production (active, description, image_url, name, price, type)
values(true, 'description3', 'image_url3', 'name3', 3, 1);

insert into type (id, name)
values(2, 'type2');

insert into production (active, description, image_url, name, price, type)
values(true, 'description1', 'image_url1', 'name4', 1, 2);

insert into production (active, description, image_url, name, price, type)
values(true, 'description2', 'image_url2', 'name5', 2, 2);

insert into production (active, description, image_url, name, price, type)
values(true, 'description3', 'image_url3', 'name6', 3, 2);

insert into production (active, description, image_url, name, price, type)
values(true, 'description4', 'image_url4', 'name7', 3, 2);

insert into type (id, name)
values(3, 'type3');