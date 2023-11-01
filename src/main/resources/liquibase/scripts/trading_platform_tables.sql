-- liquibase formatted sql

-- changeset grigorii:create-user_entity-table
CREATE TABLE user_entity (
    id                  serial PRIMARY KEY,
    username            text,
    password            text,
    first_name          text,
    last_name           text,
    phone               text,
    role                int4,
    image_entity_id     int4
);

-- changeset grigorii:create-ad_entity-table
CREATE TABLE ad_entity (
    pk                  serial PRIMARY KEY,
    price               integer,
    title               text,
    description         text,
    user_entity_id      int4,
    image_entity_id     int4
);

-- changeset grigorii:create-comment_entity-table
CREATE TABLE comment_entity (
    pk                  serial PRIMARY KEY,
    text                text,
    created_at          timestamp,
    user_entity_id      int4,
    ad_entity_id        int4
);

-- changeset grigorii:create-image_entity-table
CREATE TABLE image_entity (
    id                  serial PRIMARY KEY,
    file_path           text,
    file_size           bigint,
    media_type          text
);

-- changeset KrozhDev: insert admin data
INSERT  INTO  user_entity  (username, password, first_name, last_name, phone, role, image_entity_id)
VALUES  ('user@gmail.com', 'password', 'admin', 'admin', '89999999999', '1', '1');

INSERT  INTO  ad_entity  (price, title, description, user_entity_id, image_entity_id)
VALUES  ('100', 'something', 'something useful', '1', '1');

INSERT  INTO  comment_entity  (text, created_at, user_entity_id, ad_entity_id)
VALUES  ('some text', '2023-10-19 15:02:58.000000', '1', '1');

INSERT  INTO  comment_entity  (text, created_at, user_entity_id, ad_entity_id)
VALUES  ('some another text', '2023-10-19 15:02:58.000000', '1', '1');

INSERT  INTO  image_entity  (file_path, file_size, media_type)
VALUES  ('some/path', '10', '1');