create table if not exists purchase_order
(
    id          uuid primary key,
    customer_id uuid not null
);

create table if not exists purchase_order_product
(
    id                uuid primary key,
    purchase_order_id uuid    not null,
    product_id        uuid    not null,
    quantity          integer not null,
    purchase_price    numeric(19, 2) not null
);