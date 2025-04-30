create table users
(
    id                bigint auto_increment
        primary key,
    first_name         varchar(255)                       not null,
    last_name          varchar(255)                       not null,
    role_id            int   default 1 not null,
    created_at         datetime default current_timestamp null,
    updated_at         datetime default current_timestamp null,
    gender            varchar(50)                        null,
    profile_picture_url text      null,
    phone             varchar(255)                       not null,
    email             varchar(255)                       not null,
    password          varchar(255)                       not null,
    constraint users_pk
        unique (id),
    constraint users_pk_2
        unique (email, phone, id),
    constraint users_roles_id_fk
        foreign key (role_id) references roles (id)
);

alter table users
    modify role_id int default 1 not null;

INSERT INTO users (first_name, last_name, role_id, gender, profile_picture_url, phone, email, password, created_at, updated_at) VALUES
                                                                                                                                    ('John', 'Doe', 1, 'Male', NULL, '123-456-7890', 'john.doe@ist.com', '$2a$10$KbHq5HBBM1.ZYPa63uTxWeff8um2pyGizpcSbeF1yBvOunXBjpsZi', NOW(), NOW()),
                                                                                                                                    ('Jane', 'Smith', 2, 'Female', NULL, '987-654-3210', 'jane.smith@ist.com', '$2a$10$KbHq5HBBM1.ZYPa63uTxWeff8um2pyGizpcSbeF1yBvOunXBjpsZi', NOW(), NOW()),
                                                                                                                                    ('Robert', 'Niyitanga', 2, 'Male', NULL, '987-654-3210', 'robertwilly668@ist.com', '$2a$10$KbHq5HBBM1.ZYPa63uTxWeff8um2pyGizpcSbeF1yBvOunXBjpsZi', NOW(), NOW()),
                                                                                                                                    ('Mary', 'Johnson', 3, 'Female', NULL, '555-555-5555', 'mary.johnson@ist.com', '$2a$10$KbHq5HBBM1.ZYPa63uTxWeff8um2pyGizpcSbeF1yBvOunXBjpsZi', NOW(), NOW());