CREATE TABLE IF NOT EXISTS activity_logs (
                                                 log_id BIGSERIAL PRIMARY KEY,
                                                 log VARCHAR(60),
                                                 type VARCHAR(40),
                                                 day_id BIGINT,
                                                 CONSTRAINT fk_day FOREIGN KEY (day_id) REFERENCES days(day_id)
);

ALTER TABLE days
    ADD COLUMN log_id BIGINT;

ALTER TABLE days
    ADD CONSTRAINT fk_log FOREIGN KEY (log_id) REFERENCES activity_logs(log_id);




