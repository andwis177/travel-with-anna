CREATE TABLE IF NOT EXISTS users (
                                     user_id BIGSERIAL PRIMARY KEY,
                                     user_name VARCHAR(255) UNIQUE NOT NULL,
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

CREATE TABLE budget (
                        id BIGSERIAL PRIMARY KEY,
                        currency VARCHAR(10) NOT NULL,
                        to_spend DECIMAL(19, 2)
);

CREATE TABLE notes (
                       id BIGSERIAL PRIMARY KEY,
                       note VARCHAR(500)
);

CREATE TABLE trips (
                       id BIGSERIAL PRIMARY KEY,
                       trip_name VARCHAR(255),
                       owner_id BIGINT,
                       budget_id BIGINT,
                       note_id BIGINT,
                       FOREIGN KEY (owner_id) REFERENCES users(user_id),
                       FOREIGN KEY (budget_id) REFERENCES budget(id),
                       FOREIGN KEY (note_id) REFERENCES notes(id)
);


CREATE TABLE days (
                      id BIGSERIAL PRIMARY KEY,
                      date DATE NOT NULL,
                      note_id BIGINT,
                      trip_id BIGINT,
                      FOREIGN KEY (note_id) REFERENCES notes(id),
                      FOREIGN KEY (trip_id) REFERENCES trips(id)

);

CREATE TABLE badge (
                       id BIGINT NOT NULL PRIMARY KEY,
                       name VARCHAR(40)
);

CREATE TABLE expanses (
                          id BIGSERIAL PRIMARY KEY,
                          expanse_name VARCHAR(255),
                          currency VARCHAR(10) NOT NULL ,
                          price DECIMAL(19, 2) NOT NULL,
                          paid DECIMAL(19, 2) NOT NULL,
                          exchange_rate DECIMAL(19, 2) NOT NULL
--                               pdf_doc_id BIGINT,
--                           FOREIGN KEY (pdf_doc_id) REFERENCES pdf_doc(id)
                      );

CREATE TABLE activity (
                          id BIGINT NOT NULL PRIMARY KEY,
                          badge_id BIGINT,
                          note_id BIGINT,
                          day_id BIGINT,
                          expanse_id BIGINT,
                          FOREIGN KEY (badge_id) REFERENCES badge(id),
                          FOREIGN KEY (note_id) REFERENCES notes(id),
                          FOREIGN KEY (day_id) REFERENCES days(id),
                          FOREIGN KEY (expanse_id) REFERENCES expanses(id)

);

CREATE TABLE trip_viewer (
                             trip_id BIGINT,
                             viewer_id BIGINT,
                             PRIMARY KEY (trip_id, viewer_id),
                             FOREIGN KEY (trip_id) REFERENCES trips(id),
                             FOREIGN KEY (viewer_id) REFERENCES users(user_id)
);

CREATE TABLE backpack (
                          id BIGSERIAL PRIMARY KEY,
                          item VARCHAR(100) NOT NULL ,
                          is_packed BOOLEAN,
                          note_id BIGINT,
                          trip_id BIGINT,
                          expanse_id BIGINT,
                          FOREIGN KEY (trip_id) REFERENCES trips(id),
                          FOREIGN KEY (note_id) REFERENCES notes(id),
                          FOREIGN KEY (expanse_id) REFERENCES expanses(id)
);

CREATE TABLE pdf_doc (
                         id BIGSERIAL PRIMARY KEY,
                         name VARCHAR(255),
                         pdf TEXT
);


ALTER TABLE users
    ADD COLUMN owned_trips_id BIGINT;

ALTER TABLE users
    ADD COLUMN trips_to_view_id BIGINT;

ALTER TABLE expanses
    ADD COLUMN pdf_doc_id BIGINT;

ALTER TABLE users
    ADD FOREIGN KEY (owned_trips_id) REFERENCES trips(id);

ALTER TABLE users
    ADD FOREIGN KEY (trips_to_view_id) REFERENCES trips(id);

ALTER TABLE expanses
    ADD FOREIGN KEY (pdf_doc_id) REFERENCES pdf_doc(id);