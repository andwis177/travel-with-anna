CREATE TABLE IF NOT EXISTS users (
                                     user_id BIGSERIAL PRIMARY KEY,
                                     user_name VARCHAR(30) UNIQUE NOT NULL,
                                     email VARCHAR(255) UNIQUE NOT NULL,
                                     password VARCHAR(255) NOT NULL,
                                     account_locked BOOLEAN DEFAULT FALSE,
                                     enabled BOOLEAN DEFAULT TRUE,
                                     created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     last_modified_date TIMESTAMP,
                                     role_id INT NOT NULL,
                                     avatar_id BIGINT,
                                     CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

CREATE TABLE trips (
                       trip_id BIGSERIAL PRIMARY KEY,
                       trip_name VARCHAR(100),
                       owner_id BIGINT,
                       FOREIGN KEY (owner_id) REFERENCES users(user_id)
);

CREATE TABLE days (
                      day_id BIGSERIAL PRIMARY KEY,
                      date DATE NOT NULL
);

CREATE TABLE backpack (
                          backpack_id BIGSERIAL PRIMARY KEY
);

CREATE TABLE activity (
                          activity_id BIGINT NOT NULL PRIMARY KEY,
                          day_id BIGINT,
                          FOREIGN KEY (day_id) REFERENCES days(day_id)

);

CREATE TABLE notes (
                       note_id BIGSERIAL PRIMARY KEY,
                       note VARCHAR(500),
                       day_id BIGINT,
                       activity_id BIGINT,
                       FOREIGN KEY (day_id) REFERENCES days(day_id),
                       FOREIGN KEY (activity_id) REFERENCES activity(activity_id)
);

CREATE TABLE budget (
                        budget_id BIGSERIAL PRIMARY KEY,
                        currency VARCHAR(10),
                        to_spend DECIMAL(19, 2) NOT NULL
);

CREATE TABLE pdf (
                     pdf_id BIGSERIAL PRIMARY KEY,
                     name VARCHAR(100),
                     pdf TEXT
);

CREATE TABLE expanses (
                          expanse_id BIGSERIAL PRIMARY KEY,
                          expanse_name VARCHAR(60),
                          currency VARCHAR(10) NOT NULL ,
                          price DECIMAL(19, 2) NOT NULL,
                          paid DECIMAL(19, 2) NOT NULL,
                          exchange_rate DECIMAL(19, 5) NOT NULL,
                          trip_id BIGINT,
                          FOREIGN KEY (trip_id) REFERENCES trips(trip_id)
);

CREATE TABLE items (
                       item_id BIGSERIAL PRIMARY KEY,
                       item VARCHAR(60) NOT NULL,
                       quantity VARCHAR(40),
                       is_packed BOOLEAN DEFAULT FALSE,
                       backpack_id BIGINT,
                       FOREIGN KEY (backpack_id) REFERENCES backpack(backpack_id)
);

CREATE TABLE badge (
                       badge_id BIGINT NOT NULL PRIMARY KEY,
                       name VARCHAR(40) NOT NULL
);


ALTER TABLE trips
    ADD COLUMN backpack_id BIGINT;

ALTER TABLE trips
    ADD FOREIGN KEY (backpack_id) REFERENCES backpack(backpack_id);

ALTER TABLE trips
    ADD COLUMN budget_id BIGINT;

ALTER TABLE trips
    ADD FOREIGN KEY (budget_id) REFERENCES budget(budget_id);

--
ALTER TABLE backpack
    ADD COLUMN note_id BIGINT;

ALTER TABLE backpack
    ADD FOREIGN KEY (note_id) REFERENCES notes(note_id);

--
ALTER TABLE activity
    ADD COLUMN badge_id BIGINT;

ALTER TABLE activity
    ADD FOREIGN KEY (badge_id) REFERENCES badge(badge_id);

ALTER TABLE activity
    ADD COLUMN note_id BIGINT;

ALTER TABLE activity
    ADD FOREIGN KEY (note_id) REFERENCES notes(note_id);

ALTER TABLE activity
    ADD COLUMN expanse_id BIGINT;

ALTER TABLE activity
    ADD FOREIGN KEY (expanse_id) REFERENCES expanses(expanse_id);

--
ALTER TABLE days
    ADD COLUMN note_id BIGINT;

ALTER TABLE days
    ADD FOREIGN KEY (note_id) REFERENCES notes(note_id);

ALTER TABLE days
    ADD COLUMN trip_id BIGINT;

ALTER TABLE days
    ADD FOREIGN KEY (trip_id) REFERENCES trips(trip_id);

ALTER TABLE expanses
    ADD COLUMN pdf_pdf_id BIGINT;

ALTER TABLE expanses
    ADD FOREIGN KEY (pdf_pdf_id) REFERENCES pdf(pdf_id);

ALTER TABLE items
    ADD COLUMN expanse_id BIGINT;

ALTER TABLE items
    ADD FOREIGN KEY (expanse_id) REFERENCES expanses(expanse_id);

--
-- ALTER TABLE expanses
--     ADD COLUMN item_id BIGINT;
--
-- ALTER TABLE expanses
--     ADD FOREIGN KEY (item_id) REFERENCES items(id);
