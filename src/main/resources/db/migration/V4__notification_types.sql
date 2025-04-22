create table notification_types
(
    id         bigint auto_increment
        primary key,
    name       varchar(255)                       null,
    label      varchar(255)                       null,
    color      varchar(255)                       null,
    icon       varchar(255)                       null,
    created_at datetime default current_timestamp null,
    updated_at datetime default current_timestamp null,
    constraint notification_types_pk_2
        unique (name)
);

# INSERT INTO notification_types (name, label, color, icon, created_at, updated_at)
# VALUES
# -- Informational
# ('info', 'Information', 'text-blue-500', 'InfoCircleOutlined', NOW(), NOW()),
#
# -- Success
# ('success', 'Success', 'text-green-500', 'CheckCircleOutlined', NOW(), NOW()),
#
# -- Warning
# ('warning', 'Warning', 'text-yellow-500', 'ExclamationCircleOutlined', NOW(), NOW()),
#
# -- Error/Danger
# ('danger', 'Danger', 'text-red-500', 'CloseCircleOutlined', NOW(), NOW()),
#
# -- Reminder
# ('reminder', 'Reminder', 'text-indigo-500', 'BellOutlined', NOW(), NOW()),
#
# -- Urgent
# ('urgent', 'Urgent', 'text-rose-600', 'ThunderboltOutlined', NOW(), NOW());
