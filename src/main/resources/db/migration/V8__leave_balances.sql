CREATE TABLE leave_balances
(
    id              INT AUTO_INCREMENT PRIMARY KEY,
    employee_id     INT                             NOT NULL,
    leave_type_id   INT                             NOT NULL,
    year            INT                             NOT NULL,
    total_days      INT                             NOT NULL,
    used_days       INT DEFAULT 0                   NOT NULL,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_leave_type FOREIGN KEY (leave_type_id) REFERENCES leave_types(id),
    CONSTRAINT unique_leave_per_year UNIQUE (employee_id, leave_type_id, year)
);
