CREATE TABLE avatars (
        avatar_id BIGSERIAL PRIMARY KEY,
        avatar TEXT
);

ALTER TABLE users
    ADD COLUMN avatar_id BIGINT;
