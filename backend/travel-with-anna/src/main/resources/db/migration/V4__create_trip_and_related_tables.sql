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

CREATE TABLE IF NOT EXISTS trips  (
                                      trip_id BIGSERIAL PRIMARY KEY,
                                      trip_name VARCHAR(60),
                                      owner_id BIGINT,
                                      CONSTRAINT fk_owner FOREIGN KEY (owner_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS days (
                                    day_id BIGSERIAL PRIMARY KEY,
                                    date DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS backpack (
                                        backpack_id BIGSERIAL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS badges (
                                      badge_id SERIAL PRIMARY KEY,
                                      badge_name VARCHAR(40) NOT NULL
);


CREATE TABLE IF NOT EXISTS addresses (
                                        address_id BIGSERIAL PRIMARY KEY,
                                        place VARCHAR(100),
                                        country VARCHAR(100),
                                        country_code VARCHAR(3),
                                        city VARCHAR(100),
                                        address VARCHAR(200),
                                        website VARCHAR(255),
                                        phone VARCHAR(30),
                                        email VARCHAR(150),
                                        currency VARCHAR(10)
);


CREATE TABLE IF NOT EXISTS activities (
                                        activity_id BIGSERIAL PRIMARY KEY,
                                        activity_title VARCHAR(60),
                                        begin_time TIME,
                                        end_time TIME,
                                        badge VARCHAR(20),
                                        type VARCHAR(20),
                                        status VARCHAR(20),
                                        day_id BIGINT,
                                        associated_id BIGINT,
                                        address_id BIGINT,
                                        CONSTRAINT fk_day FOREIGN KEY (day_id) REFERENCES days(day_id),
                                        CONSTRAINT fk_address FOREIGN KEY (address_id) REFERENCES addresses(address_id)

);

CREATE TABLE IF NOT EXISTS notes (
                                     note_id BIGSERIAL PRIMARY KEY,
                                     note VARCHAR(500),
                                     day_id BIGINT,
                                     activity_id BIGINT,
                                     CONSTRAINT fk_day FOREIGN KEY (day_id) REFERENCES days(day_id),
                                     CONSTRAINT fk_activity FOREIGN KEY (activity_id) REFERENCES activities(activity_id)
);

CREATE TABLE IF NOT EXISTS budget (
                                      budget_id BIGSERIAL PRIMARY KEY,
                                      currency VARCHAR(10) NOT NULL,
                                      to_spend DECIMAL(19, 2) NOT NULL
);


CREATE TABLE IF NOT EXISTS expanses (
                                        expanse_id BIGSERIAL PRIMARY KEY,
                                        expanse_name VARCHAR(60),
                                        currency VARCHAR(10) NOT NULL ,
                                        price DECIMAL(19, 2) NOT NULL,
                                        paid DECIMAL(19, 2) NOT NULL,
                                        exchange_rate DECIMAL(19, 5) NOT NULL,
                                        trip_id BIGINT,
                                        CONSTRAINT fk_trip FOREIGN KEY (trip_id) REFERENCES trips(trip_id)
);

CREATE TABLE IF NOT EXISTS items (
                                     item_id BIGSERIAL PRIMARY KEY,
                                     item_name VARCHAR(60) NOT NULL,
                                     quantity VARCHAR(40),
                                     is_packed BOOLEAN DEFAULT FALSE,
                                     backpack_id BIGINT,
                                     CONSTRAINT fk_backpack FOREIGN KEY (backpack_id) REFERENCES backpack(backpack_id)
);


ALTER TABLE trips
    ADD COLUMN backpack_id BIGINT;

ALTER TABLE trips
    ADD CONSTRAINT fk_backpack FOREIGN KEY (backpack_id) REFERENCES backpack(backpack_id);

ALTER TABLE trips
    ADD COLUMN budget_id BIGINT;

ALTER TABLE trips
    ADD CONSTRAINT fk_budget FOREIGN KEY (budget_id) REFERENCES budget(budget_id);

--
ALTER TABLE trips
    ADD COLUMN note_id BIGINT;

ALTER TABLE trips
    ADD CONSTRAINT fk_note FOREIGN KEY (note_id) REFERENCES notes(note_id);

ALTER TABLE activities
    ADD COLUMN note_id BIGINT;

ALTER TABLE activities
    ADD CONSTRAINT fk_note FOREIGN KEY (note_id) REFERENCES notes(note_id);

ALTER TABLE activities
    ADD COLUMN expanse_id BIGINT;

ALTER TABLE activities
    ADD CONSTRAINT fk_expanse FOREIGN KEY (expanse_id) REFERENCES expanses(expanse_id);

--
ALTER TABLE days
    ADD COLUMN note_id BIGINT;

ALTER TABLE days
    ADD CONSTRAINT fk_note FOREIGN KEY (note_id) REFERENCES notes(note_id);

ALTER TABLE days
    ADD COLUMN trip_id BIGINT;

ALTER TABLE days
    ADD CONSTRAINT fk_trip FOREIGN KEY (trip_id) REFERENCES trips(trip_id);

ALTER TABLE items
    ADD COLUMN expanse_id BIGINT;

ALTER TABLE items
    ADD CONSTRAINT fk_expanse FOREIGN KEY (expanse_id) REFERENCES expanses(expanse_id);

