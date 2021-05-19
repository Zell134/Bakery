insert into users (id, activation_code, apartment, house, phone, street, username, email, password, active)
            values (1, 'null', '1', '1', '12345678912', 'street', 'admin', 'asdf@afa.ds', '1', 'true');

insert into user_role (user_id, roles)
values(1, 'ADMIN'), (1, 'USER');
