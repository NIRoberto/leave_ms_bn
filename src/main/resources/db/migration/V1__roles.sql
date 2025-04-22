create table roles
(
    id          int auto_increment
        primary key,
    name        varchar(255)                       not null,
    description text                               null,
    created_at   datetime default current_timestamp not null,
    updated_at   datetime default current_timestamp null
);

INSERT INTO roles (name, description, created_at, updated_at)
VALUES
    ('STAFF', 'General employee role with access to personal leave management features.', NOW(), NOW()),
    ('MANAGER', 'Responsible for reviewing and approving leave requests of their team.', NOW(), NOW()),
    ('ADMIN', 'System administrator with full access to user and leave settings.', NOW(), NOW());
