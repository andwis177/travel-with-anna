CREATE TABLE avatars (
        avatar_id BIGSERIAL PRIMARY KEY,
        avatar VARCHAR
);

ALTER TABLE users
    ADD COLUMN avatar_id BIGINT;

ALTER TABLE users
    ADD CONSTRAINT fk_user_avatar
        FOREIGN KEY (avatar_id) REFERENCES avatars(avatar_id);