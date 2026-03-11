CREATE INDEX idx_holidays_holiday_date
ON holidays(holiday_date);

CREATE INDEX idx_leave_requests_user_id
ON leave_requests(user_id);

CREATE INDEX idx_leave_requests_status
ON leave_requests(status);

CREATE INDEX idx_leave_requests_start_date
ON leave_requests(start_date);

CREATE INDEX idx_leave_requests_end_date
ON leave_requests(end_date);

CREATE INDEX idx_leave_balances_user_id
ON leave_balances(user_id);

CREATE INDEX idx_leave_balances_year
ON leave_balances(year);

CREATE INDEX idx_leave_status_history_leave_request_id
ON leave_status_history(leave_request_id);

CREATE INDEX idx_leave_status_history_changed_by
ON leave_status_history(changed_by);

CREATE INDEX idx_notifications_user_id
ON notifications(user_id);

CREATE INDEX idx_notifications_is_read
ON notifications(is_read);

CREATE INDEX idx_notifications_created_at
ON notifications(created_at);