drop table invitation;
drop table category_transaction;
drop table person_transaction;
drop table person;
drop table category;
drop table team;



create table team
(
    id int generated by default as identity primary key,
    name varchar(50) not null
);

create table category
(
    id int generated by default as identity primary key,
    name varchar(50) not null
);

create table person
(
    id int generated by default as identity primary key,
    team_id int references team (id) on delete set null,
    full_name varchar(50) not null,
    email varchar(50) not null unique,
    password varchar(100) not null,
    balance float not null check (balance >= 0),
    gender varchar(1) not null check (gender IN ('F', 'M')),
    role varchar(20) not null check (role IN ('ROLE_USER', 'ROLE_LEADER'))
);

create table category_transaction
(
    id int generated by default as identity primary key,
    person_id int references person (id) on delete cascade,
    category_id int references category (id) on delete set null,
    amount float not null,
    created_at date not null,
    description varchar(50)
);

create table person_transaction
(
    id int generated by default as identity primary key,
    person_from_id int references person (id) on delete set null,
    person_to_id int references person (id) on delete set null,
    amount float not null check (amount > 0),
    created_at date not null,
    description varchar(50),
    check (person_from_id <> person_to_id)
);

create table invitation
(
    id int generated by default as identity primary key,
    person_from_id int references person (id) on delete cascade,
    person_to_id int references person (id) on delete cascade,
    check (person_from_id <> person_to_id),
    unique (person_from_id, person_to_id)
);



insert into team("name") values ('Семья Ивановых');
insert into team("name") values ('Семья Гусевых');
insert into team("name") values ('Семья Михайловых');
insert into team("name") values ('Семья Алексеевых');
insert into team("name") values ('Семья Кабановых');

insert into category("name") values ('Продуктовый магазин');
insert into category("name") values ('Хозтовары');
insert into category("name") values ('Подписка на музыкальный сервис');
insert into category("name") values ('Магазин электроники');
insert into category("name") values ('Магазин сантехники');
insert into category("name") values ('Кафе/Ресторан');
insert into category("name") values ('Заработная плата');
insert into category("name") values ('Премия');
insert into category("name") values ('Пенсия');
insert into category("name") values ('Услуги ЖКХ');

insert into person(team_id, full_name, email, "password", balance, gender, role) values (1, 'Майя Иванова','nesarimmena-1871@yopmail.com','5V6bObID3R', 12864,'F', 'ROLE_LEADER');
insert into person(team_id, full_name, email, "password", balance, gender, role) values (1, 'Никита Иванов','ciritrumibeu-2755@yopmail.com','AYqpPyZOWY', 11456,'M', 'ROLE_USER');
insert into person(team_id, full_name, email, "password", balance, gender, role) values (1, 'Вероника Иванова','mabimomeuke-9014@yopmail.com','slhroLaHhX', 13299,'F', 'ROLE_USER');
insert into person(team_id, full_name, email, "password", balance, gender, role) values (1, 'Анастасия Иванова','tretrutrekaunou-8088@yopmail.com','J12X9spDK5', 16417,'F', 'ROLE_USER');
insert into person(team_id, full_name, email, "password", balance, gender, role) values (1, 'Ярослав Иванов','laheureufroxe-7505@yopmail.com','NC254oZaZ9', 10000,'M', 'ROLE_USER');
insert into person(team_id, full_name, email, "password", balance, gender, role) values (2, 'Ульяна Гусева','raquilecroikei-7468@yopmail.com','E7yUTVjMOR', 19204,'F', 'ROLE_LEADER');
insert into person(team_id, full_name, email, "password", balance, gender, role) values (2, 'Давид Гусев','wufacunnutri-8011@yopmail.com','VfkSDRLmxq', 16417,'M', 'ROLE_USER');
insert into person(team_id, full_name, email, "password", balance, gender, role) values (2, 'Андрей Гусев','ponijuvaupe-9101@yopmail.com','FSDnde0qWm', 13299,'M', 'ROLE_USER');
insert into person(team_id, full_name, email, "password", balance, gender, role) values (3, 'Данила Михайлов','danila_master2002@yopmail.com','aHrIpFsS5A', 11456,'M', 'ROLE_LEADER');
insert into person(team_id, full_name, email, "password", balance, gender, role) values (3, 'Дарья Михайлова','fokafiffuvou-3117@yopmail.com','RCSJbkmTRX', 12864,'F', 'ROLE_USER');
insert into person(team_id, full_name, email, "password", balance, gender, role) values (4, 'Андрей Алексеев','cejemmoivoyu-3202@yopmail.com','6cpl93TP5I', 19204,'M', 'ROLE_LEADER');
insert into person(team_id, full_name, email, "password", balance, gender, role) values (4, 'Анастасия Алексеева','xoucroquoigraye-4686@yopmail.com','3b8JHShfqw', 12864,'F', 'ROLE_USER');
insert into person(team_id, full_name, email, "password", balance, gender, role) values (5, 'Фёдор Кабанов','docriyoppakei-5138@yopmail.com','08IXbV6f', 19204,'M', 'ROLE_LEADER');
insert into person(team_id, full_name, email, "password", balance, gender, role) values (5, 'Александра Кабанов','vouvattakowau-8082@yopmail.com','TBRq5BZG', 19204,'M', 'ROLE_USER');
insert into person(team_id, full_name, email, "password", balance, gender, role) values (null, 'Дмитрий Иванов','doudle_jump2003@mail.ru','pYyL3Xaw', 50000,'M', 'ROLE_USER');

insert into person_transaction (person_from_id, person_to_id, amount, created_at, description) values (5, 11, 2500,'2023-05-30', 'С днём рождения!');
insert into person_transaction (person_from_id, person_to_id, amount, created_at, description) values (7, 8, 300,'2023-05-30', 'Долг');
insert into person_transaction (person_from_id, person_to_id, amount, created_at) values (5, 10, 500,'2023-05-30');
insert into person_transaction (person_from_id, person_to_id, amount, created_at) values (10, 11, 2500,'2023-05-30');

insert into category_transaction (person_id, category_id, amount, description, created_at) values (1, 1, 326, 'Сыр, батон, молоко, колбаса','2023-03-05');
insert into category_transaction (person_id, category_id, amount, created_at) values (15, 3, 500, '2023-06-05');
insert into category_transaction (person_id, category_id, amount, created_at) values (9, 6, 1000, '2023-06-07');
insert into category_transaction (person_id, category_id, amount, created_at) values (5, 2, 314, '2023-06-08');
insert into category_transaction (person_id, category_id, amount, created_at) values (5, 9, 7000, '2023-06-11');
insert into category_transaction (person_id, category_id, amount, created_at) values (9, 7, 15000, '2023-06-15');

insert into invitation(person_from_id, person_to_id) values (1,2);
insert into invitation(person_from_id, person_to_id) values (1,3);
insert into invitation(person_from_id, person_to_id) values (1,4);
insert into invitation(person_from_id, person_to_id) values (1,5);
insert into invitation(person_from_id, person_to_id) values (6,7);
insert into invitation(person_from_id, person_to_id) values (9,10);
insert into invitation(person_from_id, person_to_id) values (11,12);
insert into invitation(person_from_id, person_to_id) values (13,14);