CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    employee_code VARCHAR(100) NOT NULL,
    full_name VARCHAR(255),
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255),
    role VARCHAR(50),
    gender VARCHAR(50),
    created_at DATETIME(6),
    updated_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_users_employee_code UNIQUE (employee_code),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE holidays (
    id BIGINT NOT NULL AUTO_INCREMENT,
    holiday_name VARCHAR(255) NOT NULL,
    holiday_date DATE NOT NULL,
    holiday_type VARCHAR(50),
    description TEXT,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_holidays_holiday_date UNIQUE (holiday_date)
);

CREATE TABLE leave_requests (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    leave_type VARCHAR(50),
    start_date DATE,
    end_date DATE,
    reason TEXT,
    status VARCHAR(50),
    manager_comment TEXT,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_leave_requests_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE leave_balances (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    year INT NOT NULL,
    total_leaves INT,
    used_leaves INT,
    pending_leaves INT,
    remaining_leaves INT,
    maternity_total INT,
    maternity_used INT,
    maternity_pending INT,
    maternity_remaining INT,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_leave_balances_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT uk_leave_balances_user_year UNIQUE (user_id, year)
);

CREATE TABLE leave_status_history (
    id BIGINT NOT NULL AUTO_INCREMENT,
    leave_request_id BIGINT NOT NULL,
    old_status VARCHAR(50),
    new_status VARCHAR(50),
    changed_by BIGINT NOT NULL,
    comment TEXT,
    changed_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_leave_status_history_leave_request
        FOREIGN KEY (leave_request_id) REFERENCES leave_requests(id),
    CONSTRAINT fk_leave_status_history_changed_by
        FOREIGN KEY (changed_by) REFERENCES users(id)
);

CREATE TABLE notifications (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    is_read BIT(1) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id)
);