create table if not exists customer
(
    id    uuid primary key,
    name  varchar(100) not null,
    email varchar(150) not null
);