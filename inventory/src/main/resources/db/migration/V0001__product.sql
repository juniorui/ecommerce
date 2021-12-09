create table if not exists product
(
    id    uuid primary key,
    name  varchar(100)   not null,
    price numeric(19, 2) not null
);