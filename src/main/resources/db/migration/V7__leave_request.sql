create table leave_requests
(
    id              bigint auto_increment primary key,
    user_id         bigint                                not null,
    leave_type_id   int                                not null,
    reviewer_by_id  bigint                                not null,
    start_date      datetime                           not null,
    end_date        datetime                           not null,
    duration        int                                not null,
    created_at      datetime default current_timestamp null,
    update_at       datetime default current_timestamp null,
    leave_status_id int         default  1                       not null,

    -- Foreign keys
    constraint fk_leave_requests_user
        foreign key (user_id) references users (id),
    constraint fk_leave_requests_leave_type
        foreign key (leave_type_id) references leave_types (id),
    constraint fk_leave_requests_reviewer
        foreign key (reviewer_by_id) references users (id),
    constraint fk_leave_requests_status
        foreign key (leave_status_id) references leave_statuses (id)
);


INSERT INTO leave_requests
(user_id, leave_type_id, reviewer_by_id, start_date, end_date, duration, leave_status_id, created_at, update_at)
VALUES
    (1, 1, 3, '2025-05-01 08:00:00', '2025-05-03 17:00:00', 3, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- pending
    (2, 2, 3, '2025-06-10 08:00:00', '2025-06-12 17:00:00', 3, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- approved
    (1, 1, 3, '2025-04-15 08:00:00', '2025-04-16 17:00:00', 2, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- rejected
    (2, 2, 3, '2025-07-01 08:00:00', '2025-07-05 17:00:00', 5, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- cancelled
    (3, 1, 2, '2025-07-10 08:00:00', '2025-07-11 17:00:00', 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- approved
    (1, 2, 2, '2025-08-20 08:00:00', '2025-08-22 17:00:00', 3, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP); -- pending
