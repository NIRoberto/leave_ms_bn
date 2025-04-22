create table leave_types
(
    id                bigint auto_increment
        primary key,
    name              varchar(255)                       not null,
    description       text                               null,
    max_days_per_year int                                not null,
    is_paid           bool     default true              not null,
    created_at        datetime default current_timestamp null,
    updated_at        datetime default current_timestamp not null,
    constraint leave_types_pk_2
        unique (name)
);
#
# INSERT INTO leave_types (name, description, max_days_per_year, is_paid, created_at, updated_at)
# VALUES
# -- Annual Leave
# ('Annual Leave', 'Paid time off typically granted each year for vacation or rest.', 21, true, NOW(), NOW()),
#
# -- Sick Leave
# ('Sick Leave', 'Time off given when an employee is ill or injured.', 10, true, NOW(), NOW()),
#
# -- Maternity Leave
# ('Maternity Leave', 'Leave granted to a mother before and after childbirth.', 90, true, NOW(), NOW()),
#
# -- Paternity Leave
# ('Paternity Leave', 'Leave granted to a father after the birth of a child.', 10, true, NOW(), NOW()),
#
# -- Compassionate Leave
# ('Compassionate Leave', 'Leave for personal emergencies such as a death in the family.', 5, true, NOW(), NOW()),
#
# -- Study Leave
# ('Study Leave', 'Leave granted for academic or training purposes.', 15, false, NOW(), NOW()),
#
# -- Unpaid Leave
# ('Unpaid Leave', 'Time off without pay for personal reasons.', 30, false, NOW(), NOW());
