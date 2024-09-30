CREATE SEQUENCE IF NOT EXISTS activity_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS traveling (
                                         activity_id BIGINT PRIMARY KEY,
                                         travel_type VARCHAR(40) NOT NULL,
                                         CONSTRAINT fk_activity FOREIGN KEY (activity_id) REFERENCES activity (activity_id)
);


CREATE TABLE IF NOT EXISTS accommodation (
                                             activity_id BIGINT PRIMARY KEY,
                                             accommodation_type VARCHAR(40) NOT NULL,
                                             CONSTRAINT fk_activity FOREIGN KEY (activity_id) REFERENCES activity(activity_id)
);
