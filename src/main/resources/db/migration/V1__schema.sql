CREATE TABLE orders
(
    id     bigint(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    status varchar(8)
);

CREATE TABLE order_items
(
    id       bigint(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    amount   decimal(18, 2),
    order_id bigint(20)
);