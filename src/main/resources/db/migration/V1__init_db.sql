create table order_element (
    id int8 not null,
    price numeric(19, 2), 
    quantity int4 not null, 
    product_id int8, 
    element_id int8, 
    primary key (id)
);

create table orders (
    id int8 not null, 
    destination varchar(255), 
    full_price numeric(19, 2), 
    order_date timestamp, 
    wishes varchar(255), 
    user_id int8, 
    primary key (id)
);

create table production (
    id int8 not null, 
    active boolean not null, 
    description varchar(2048), 
    image_url varchar(255), 
    name varchar(255), 
    price numeric(19, 2) not null, 
    type int8 not null, 
    primary key (id)
);

create table type (
    id int8 not null, 
    name varchar(255), 
    primary key (id)
);

create table user_role (
    user_id int8 not null, 
    roles varchar(255)
);

create table users (
    id int8 not null,   
    activation_code varchar(255), 
    active boolean not null, 
    apartment int4 not null, 
    email varchar(255) not null, 
    house varchar(255), 
    password varchar(255) not null, 
    phone varchar(255), 
    street varchar(255), 
    username varchar(255) not null, 
    primary key (id)
);

alter table if exists order_element 
    add constraint order_element_product_fk 
    foreign key (product_id) references production;

alter table if exists order_element 
    add constraint order_element_order_fk 
    foreign key (element_id) references orders;

alter table if exists orders 
    add constraint orders_user_fk 
    foreign key (user_id) references users;

alter table if exists user_role 
    add constraint user_role_fk 
    foreign key (user_id) references users;
