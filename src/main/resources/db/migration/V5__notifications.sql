create table notifications
(
    id                   bigint auto_increment
        primary key,
    user_id              bigint                not null,
    notification_type_id int                not null,
    message              varchar(1000)      not null,
    is_read              bool default false not null,
    created_at           datetime           not null,
    constraint notifications_notification_types_id_fk
        foreign key (notification_type_id) references notification_types (id)
            on delete cascade,
    constraint notifications_users_id_fk
        foreign key (user_id) references users (id)
            on delete cascade
);


-- Notification for new info message
INSERT INTO notifications (user_id, notification_type_id, message, is_read, created_at)
VALUES (1, 1, 'Welcome to the IST system!', false, NOW());

-- Notification for successful leave request
INSERT INTO notifications (user_id, notification_type_id, message, is_read, created_at)
VALUES (2, 2, 'Your leave request has been approved.', false, NOW());

-- Warning about missing profile data
INSERT INTO notifications (user_id, notification_type_id, message, is_read, created_at)
VALUES (3, 3, 'Please complete your profile information.', false, NOW());

-- Danger alert about account activity
INSERT INTO notifications (user_id, notification_type_id, message, is_read, created_at)
VALUES (1, 4, 'Suspicious login attempt detected on your account.', false, NOW());

-- Read notification example
INSERT INTO notifications (user_id, notification_type_id, message, is_read, created_at)
VALUES (2, 1, 'Your password was successfully changed.', true, NOW());

-- Info notification
INSERT INTO notifications (user_id, notification_type_id, message, is_read, created_at)
VALUES (3, 1, 'Your profile has been updated successfully.', false, NOW());
