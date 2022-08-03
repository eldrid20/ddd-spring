CREATE TABLE payment
(
    id       bigint(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    status   varchar(8)
);

CREATE TABLE payment_items
(
    id        bigint(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    amount     decimal(18, 2),
    payment_id bigint(20)
);