create table roles
(
    id          bigint auto_increment
        primary key,
    name        varchar(255)                       not null,
    description text                               null,
    createdAt   datetime default current_timestamp not null,
    updatedAt   datetime default current_timestamp null
);
