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
                       id BIGSERIAL PRIMARY KEY,
                       trip_name VARCHAR(100),
                       owner_id BIGINT,
                       FOREIGN KEY (owner_id) REFERENCES users(user_id)
);

CREATE TABLE days (
                      id BIGSERIAL PRIMARY KEY,
                      date DATE NOT NULL
);

CREATE TABLE backpack (
                          id BIGSERIAL PRIMARY KEY
);

CREATE TABLE activity (
                          id BIGINT NOT NULL PRIMARY KEY,
                          day_id BIGINT,
                          FOREIGN KEY (day_id) REFERENCES days(id)

);

CREATE TABLE notes (
                       id BIGSERIAL PRIMARY KEY,
                       note VARCHAR(500),
                       day_id BIGINT,
                       activity_id BIGINT,
                       FOREIGN KEY (day_id) REFERENCES days(id),
                       FOREIGN KEY (activity_id) REFERENCES activity(id)
);

CREATE TABLE budget (
                        id BIGSERIAL PRIMARY KEY,
                        currency VARCHAR(10) NOT NULL,
                        to_spend DECIMAL(19, 2) NOT NULL
);

CREATE TABLE pdf_doc (
                         id BIGSERIAL PRIMARY KEY,
                         name VARCHAR(100),
                         pdf TEXT
);

CREATE TABLE expanses (
                          id BIGSERIAL PRIMARY KEY,
                          expanse_name VARCHAR(255),
                          currency VARCHAR(10) NOT NULL ,
                          price DECIMAL(19, 2) NOT NULL,
                          paid DECIMAL(19, 2) NOT NULL,
                          exchange_rate DECIMAL(19, 2) NOT NULL,
                          pdf_doc_id BIGINT,
                          FOREIGN KEY (pdf_doc_id) REFERENCES pdf_doc(id)
);

CREATE TABLE items (
                       id BIGSERIAL PRIMARY KEY,
                       item VARCHAR(60) NOT NULL,
                       quantity INT,
                       is_packed BOOLEAN DEFAULT FALSE,
                       expanse_id BIGINT,
                       backpack_id BIGINT,
                       FOREIGN KEY (expanse_id) REFERENCES expanses(id),
                       FOREIGN KEY (backpack_id) REFERENCES backpack(id)
);

CREATE TABLE trip_viewer (
                             trip_id BIGINT,
                             viewer_id BIGINT,
                             PRIMARY KEY (trip_id, viewer_id),
                             FOREIGN KEY (trip_id) REFERENCES trips(id),
                             FOREIGN KEY (viewer_id) REFERENCES users(user_id)
);

CREATE TABLE badge (
                       id BIGINT NOT NULL PRIMARY KEY,
                       name VARCHAR(40) NOT NULL
);


ALTER TABLE trips
    ADD COLUMN backpack_id BIGINT;

ALTER TABLE trips
    ADD FOREIGN KEY (backpack_id) REFERENCES backpack(id);

ALTER TABLE trips
    ADD COLUMN budget_id BIGINT;

ALTER TABLE trips
    ADD FOREIGN KEY (budget_id) REFERENCES budget(id);

--
ALTER TABLE backpack
    ADD COLUMN note_id BIGINT;

ALTER TABLE backpack
    ADD FOREIGN KEY (note_id) REFERENCES notes(id);

--
ALTER TABLE activity
    ADD COLUMN badge_id BIGINT;

ALTER TABLE activity
    ADD FOREIGN KEY (badge_id) REFERENCES badge(id);

ALTER TABLE activity
    ADD COLUMN note_id BIGINT;

ALTER TABLE activity
    ADD FOREIGN KEY (note_id) REFERENCES notes(id);

ALTER TABLE activity
    ADD COLUMN expanse_id BIGINT;

ALTER TABLE activity
    ADD FOREIGN KEY (expanse_id) REFERENCES expanses(id);

--
ALTER TABLE days
    ADD COLUMN note_id BIGINT;

ALTER TABLE days
    ADD FOREIGN KEY (note_id) REFERENCES notes(id);

ALTER TABLE days
    ADD COLUMN trip_id BIGINT;

ALTER TABLE days
    ADD FOREIGN KEY (trip_id) REFERENCES trips(id);

--
ALTER TABLE expanses
    ADD COLUMN item_id BIGINT;

ALTER TABLE expanses
    ADD FOREIGN KEY (item_id) REFERENCES items(id);

ALTER TABLE expanses
    ADD COLUMN activity_id BIGINT;

ALTER TABLE expanses
    ADD FOREIGN KEY (activity_id) REFERENCES activity(id);
