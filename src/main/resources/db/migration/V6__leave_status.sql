CREATE TABLE leave_statuses (
                                id          INT AUTO_INCREMENT PRIMARY KEY,
                                name        VARCHAR(100) NOT NULL,
                                label       VARCHAR(100) NOT NULL,
                                color       VARCHAR(50)  NOT NULL,
                                created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
                                updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP
);


INSERT INTO leave_statuses (name, label, color) VALUES
                                                    ('pending',   'Pending',   'bg-yellow-400'),
                                                    ('approved',  'Approved',  'bg-green-500'),
                                                    ('rejected',  'Rejected',  'bg-red-500'),
                                                    ('cancelled', 'Cancelled', 'bg-gray-400');
