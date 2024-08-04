CREATE TABLE users (
                       user_id BIGSERIAL PRIMARY KEY,
                       user_name VARCHAR(255) UNIQUE NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       account_locked BOOLEAN DEFAULT FALSE,
                       enabled BOOLEAN DEFAULT TRUE,
                       created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       last_modified_date TIMESTAMP
);

CREATE TABLE roles (
                       role_id SERIAL PRIMARY KEY,
                       role_name VARCHAR(255) UNIQUE NOT NULL,
                       created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       last_modified_date TIMESTAMP
);


CREATE TABLE users_roles (
                            role_id INT REFERENCES roles(role_id) ON DELETE CASCADE,
                            user_id BIGINT REFERENCES users(user_id) ON DELETE CASCADE,
                            PRIMARY KEY (user_id, role_id)
);

CREATE TABLE tokens (
                        token_id BIGSERIAL PRIMARY KEY,
                        token VARCHAR(255),
                        created_at TIMESTAMP,
                        expires_at TIMESTAMP,
                        user_id BIGINT NOT NULL,
                        FOREIGN KEY (user_id) REFERENCES users(user_id)
);